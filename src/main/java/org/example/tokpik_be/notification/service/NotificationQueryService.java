package org.example.tokpik_be.notification.service;

import static java.util.Comparator.comparing;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.NotificationException;
import org.example.tokpik_be.notification.domain.Notification;
import org.example.tokpik_be.notification.domain.NotificationTalkTopic;
import org.example.tokpik_be.notification.dto.response.NotificationDetailResponse;
import org.example.tokpik_be.notification.dto.response.NotificationDetailResponse.NotificationTalkTopicResponse;
import org.example.tokpik_be.notification.dto.response.NotificationScheduledResponse;
import org.example.tokpik_be.notification.dto.response.NotificationsResponse;
import org.example.tokpik_be.notification.event.NotificationSendEvent;
import org.example.tokpik_be.notification.repository.NotificationRepository;
import org.example.tokpik_be.notification.repository.QueryDslNotificationRepository;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;
    private final QueryDslNotificationRepository queryDslNotificationRepository;

    private final UserQueryService userQueryService;
    private final ApplicationEventPublisher eventPublisher;

    public Notification findById(long notificationId) {

        return notificationRepository.findById(notificationId)
            .orElseThrow(() -> new GeneralException(NotificationException.NOTIFICATION_NOT_FOUND));
    }

    public NotificationsResponse getNotifications(long userId, Long nextCursorId) {
        int pageSize = 10;

        return queryDslNotificationRepository.getNotifications(userId, nextCursorId, pageSize);
    }

    public NotificationDetailResponse getNotificationDetail(long userId, long notificationId) {
        User user = userQueryService.findById(userId);
        Notification notification = findById(notificationId);

        if (!notification.getUser().equals(user)) {
            throw new GeneralException(NotificationException.UNAUTHORIZED_NOTIFICATION_ACCESS);
        }

        List<LocalTime> noticeTimes = generateTimeIntervals(notification.getStartTime(),
            notification.getEndTime(),
            notification.getIntervalMinutes());
        List<TalkTopic> talkTopics = notification.getNotificationTalkTopics().stream()
            .map(NotificationTalkTopic::getTalkTopic)
            .toList();

        // 각 알림 대화 주제와 송신 일시 매핑
        List<NotificationTalkTopicResponse> talkTopicResponses = new ArrayList<>();
        for (int index = 0; index < noticeTimes.size(); index++) {
            int talkTopicIndex = index % talkTopics.size();

            TalkTopic talkTopic = talkTopics.get(talkTopicIndex);
            LocalTime noticeTime = noticeTimes.get(index);
            NotificationTalkTopicResponse talkTopicResponse = new NotificationTalkTopicResponse(
                talkTopic.getTitle(),
                talkTopic.getTopicTag().getId(),
                talkTopic.getTopicTag().getContent(),
                noticeTime);

            talkTopicResponses.add(talkTopicResponse);
        }

        return new NotificationDetailResponse(notification.getName(),
            notification.getStartTime(),
            notification.getEndTime(),
            notification.getIntervalMinutes(),
            talkTopicResponses);
    }


    /**
     * 1분마다 알림 송신을 수행하는 스케줄러, 이하 과정에 따라 작업 수행 <br/> <br/>
     * 1. 현재 일시가 알림 시작 시간/종료 시간에 포함되는 알림들 조회 <br/>
     * 2. 조회된 알림들을 알림 간격(분 단위)에 따라 grouping <br/>
     * 3. 조회된 알림들 중 알림 지정 순서에 따라 최종적으로 송신될 알림 필터링 <br/>
     * 4. 알림 송신 이벤트 publish
     */
    @Async
    @Scheduled(fixedRate = 1000 * 60)
    public void sendScheduledNotifications() {
        LocalDateTime now = LocalDateTime.now();

        List<NotificationScheduledResponse> scheduledNotifications = queryDslNotificationRepository
            .getScheduledNotifications(now);
        Map<Integer, List<NotificationScheduledResponse>> groupByInterval = scheduledNotifications
            .stream()
            .collect(Collectors.groupingBy(NotificationScheduledResponse::intervalMinutes));

        LocalTime noticeTime = now.toLocalTime();
        List<NotificationSendEvent> events = groupByInterval.entrySet().stream()
            .flatMap(entry -> filterNotificationToSend(entry.getValue(), noticeTime).stream())
            .toList();

        events.forEach(eventPublisher::publishEvent);
    }

    /**
     * 최종적으로 송신할 알림들을 필터링하는 로직, 이하 과정 수행 <br/>
     * 1. 현재 시간이 송신할 시간인 알림들 필터링 <br/>
     * 2. 알림 ID에 따라 알림들 grouping <br/>
     * 3. 알림 지정 순서(알림 대화 주제 ID)에 따라 그룹별 알림들 정렬 <br/>
     * 4. 송신 일시에 해당하는 알림들 내에서, 알림 지정 순서에 따라 송신할 알림들 선택
     *
     * @param notifications 시작 시간 ~ 종료 시간 내에 알림(송신) 일시가 포함되는 알림 목록
     * @param noticeTime    알림 일시(송신 일시)
     * @return 송신할 알림 목록, 알림 송신 이벤트로 변환하여 제공
     */
    private List<NotificationSendEvent> filterNotificationToSend(
        List<NotificationScheduledResponse> notifications,
        LocalTime noticeTime) {
        Map<Long, List<NotificationScheduledResponse>> groupByNotificationId = notifications.stream()
            .filter(notification -> isNoticeTime(notification, noticeTime))
            .collect(Collectors.groupingBy(NotificationScheduledResponse::notificationId));

        groupByNotificationId.forEach((notificationId, group) ->
            group.sort(comparing(NotificationScheduledResponse::notificationTalkTopicId)));

        return groupByNotificationId.values().stream()
            .map(group -> selectNotificationToSend(group, noticeTime))
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * 특정 알림과 연관된 DTO 그룹에서 알림 지정 순서에 따라 현재 송신할 알림을 선택하는 로직
     *
     * @param group      특정 알림과 관련된 DTO 그룹
     * @param noticeTime 알림 일시(송신 일시)
     * @return 송신할 알림 정보, 알림 송신 이벤트로 변환하여 제공
     */
    private NotificationSendEvent selectNotificationToSend(
        List<NotificationScheduledResponse> group,
        LocalTime noticeTime) {

        if (group.isEmpty()) {

            return null;
        }

        NotificationScheduledResponse firstNotification = group.get(0);
        List<LocalTime> timeIntervals = generateTimeIntervals(firstNotification.startTime(),
            firstNotification.endTime(),
            firstNotification.intervalMinutes());

        int timeIndex = timeIntervals.indexOf(noticeTime);
        if (timeIndex == -1) {

            return null;
        }

        int notificationIndex = timeIndex % group.size();
        NotificationScheduledResponse sendNotification = group.get(notificationIndex);

        return new NotificationSendEvent(sendNotification.receiverToken(),
            sendNotification.talkTopicTitle(),
            sendNotification.talkTopicSubtitle());
    }

    /**
     * 송신 알림 여부 확인 로직 <br/>
     * 시작 시간부터 분 간격에 따라 알림 시간대를 구했을 때 송신 일시에 해당하는 것이 있는 경우 true 반환
     *
     * @param notification 알림
     * @param noticeTime   알림 일시(송신 일시)
     * @return 알림 일시(송신 일시) 해당 여부
     */
    private boolean isNoticeTime(NotificationScheduledResponse notification, LocalTime noticeTime) {
        LocalTime startTime = notification.startTime();
        int intervalMinutes = notification.intervalMinutes();
        long minutesSinceStart = ChronoUnit.MINUTES.between(startTime, noticeTime);

        return minutesSinceStart % intervalMinutes == 0;
    }

    /**
     * 분 간격에 따라 나눠진 시간 목록을 제공하는 로직
     *
     * @param from            시작 시간
     * @param to              종료 시간
     * @param intervalMinutes 분 간격
     * @return 분 간격에 따라 시작 시간 ~ 종료 시간내 나눠진 시간 목록
     */
    private List<LocalTime> generateTimeIntervals(LocalTime from, LocalTime to,
        int intervalMinutes) {
        List<LocalTime> timeIntervals = new ArrayList<>();

        while (from.isBefore(to)) {
            timeIntervals.add(from);
            from = from.plusMinutes(intervalMinutes);
        }

        return timeIntervals;
    }
}
