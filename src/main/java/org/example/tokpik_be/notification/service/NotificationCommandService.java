package org.example.tokpik_be.notification.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.NotificationException;
import org.example.tokpik_be.exception.ScrapException;
import org.example.tokpik_be.notification.domain.Notification;
import org.example.tokpik_be.notification.domain.NotificationTalkTopic;
import org.example.tokpik_be.notification.dto.request.NotificationCreateRequest;
import org.example.tokpik_be.notification.repository.NotificationRepository;
import org.example.tokpik_be.notification.repository.NotificationTalkTopicRepository;
import org.example.tokpik_be.scrap.domain.Scrap;
import org.example.tokpik_be.scrap.domain.ScrapTopic;
import org.example.tokpik_be.scrap.repository.ScrapRepository;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.talk_topic.repository.TalkTopicRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationCommandService {

    private final List<Integer> notificationIntervalMinutes = List.of(5, 10, 15, 20);

    private final NotificationRepository notificationRepository;
    private final NotificationTalkTopicRepository notificationTalkTopicRepository;
    private final ScrapRepository scrapRepository;
    private final TalkTopicRepository talkTopicRepository;

    private final NotificationQueryService notificationQueryService;
    private final UserQueryService userQueryService;


    public void deleteNotification(long userId, long notificationId) {
        Notification notification = notificationQueryService.findById(notificationId);
        User user = userQueryService.findById(userId);

        if (!notification.getUser().equals(user)) {
            throw new GeneralException(NotificationException.UNAUTHORIZED_NOTIFICATION_DELETION);
        }

        notification.delete();
    }

    @Scheduled(cron = "0 0 0 L 3,6,9,12 ?")
    public void deleteNotifications() {
        List<Notification> notifications = notificationRepository.findAllByDeletedIsTrue();
        List<Long> notificationIds = notifications.stream().map(Notification::getId).toList();
        notificationRepository.deleteAllByIdInBatch(notificationIds);

        List<NotificationTalkTopic> notificationTalkTopics = notificationTalkTopicRepository
            .findAllByNotificationIdIn(notificationIds);
        List<Long> notificationTalkTopicIds = notificationTalkTopics.stream()
            .map(NotificationTalkTopic::getId)
            .toList();
        notificationTalkTopicRepository.deleteAllByIdInBatch(notificationTalkTopicIds);
    }

    public void createNotification(long userId, NotificationCreateRequest request) {
        User user = userQueryService.findById(userId);

        Scrap scrap = scrapRepository.findById(request.scrapId())
            .orElseThrow(() -> new GeneralException(ScrapException.SCRAP_NOT_FOUND));

        // 요청한 사용자의 스크랩인 지 검증
        if (!scrap.getUser().equals(user)) {
            throw new GeneralException(ScrapException.UNAUTHORIZED_SCRAP_ACCESS);
        }

        // 요청된 알림 간격 검증
        int intervalMinutes = request.notificationIntervalMinutes();
        if (notificationIntervalMinutes.contains(intervalMinutes)) {
            throw new GeneralException(NotificationException.INVALID_NOTIFICATION_INTERVAL);
        }

        // 알림 생성 및 저장
        Notification notification = new Notification(request.noticeDate(),
            request.notificationStartTime(),
            request.notificationEndTime(),
            intervalMinutes,
            user,
            scrap);
        notificationRepository.save(notification);

        // 알림 대화 주제 ID들 지정한 스크랩 포함 여부, 개수 검증
        List<Long> notificationTalkTopicIds =
            request.notificationTalkTopicIds();
        List<Long> talkTopicIds = scrap.getScrapTopics().stream()
            .map(ScrapTopic::getTalkTopic)
            .map(TalkTopic::getId)
            .toList();
        List<Long> filteredIds = notificationTalkTopicIds.stream()
            .filter(talkTopicIds::contains)
            .toList();
        if (filteredIds.size() != notificationTalkTopicIds.size()) {
            throw new GeneralException(NotificationException.CAN_NOTICE_TALK_TOPICS_IN_SCRAP);
        }

        // 알림 대화 주제 생성 및 저장, 연관관계 설정
        long notificationId = notification.getId();
        List<TalkTopic> talkTopics = talkTopicRepository.findAllById(notificationTalkTopicIds);
        List<NotificationTalkTopic> notificationTalkTopics = talkTopics.stream()
            .map(talkTopic -> new NotificationTalkTopic(notificationId, talkTopic))
            .toList();
        notificationTalkTopicRepository.saveAll(notificationTalkTopics);
        notification.addNotificationTalkTopics(notificationTalkTopics);
    }
}
