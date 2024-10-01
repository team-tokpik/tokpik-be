package org.example.tokpik_be.notification.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.NotificationException;
import org.example.tokpik_be.notification.domain.Notification;
import org.example.tokpik_be.notification.dto.response.NotificationScheduledResponse;
import org.example.tokpik_be.notification.dto.response.NotificationsResponse;
import org.example.tokpik_be.notification.event.NotificationSendEvent;
import org.example.tokpik_be.notification.repository.NotificationRepository;
import org.example.tokpik_be.notification.repository.QueryDslNotificationRepository;
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
    private final ApplicationEventPublisher eventPublisher;

    public Notification findById(long notificationId) {

        return notificationRepository.findById(notificationId)
            .orElseThrow(() -> new GeneralException(NotificationException.NOTIFICATION_NOT_FOUND));
    }

    public NotificationsResponse getNotifications(long userId, Long nextCursorId) {
        int pageSize = 10;

        return queryDslNotificationRepository.getNotifications(userId, nextCursorId, pageSize);
    }

    @Async
    @Scheduled(fixedRate = 1000 * 60)
    public void sendScheduledNotifications() {
        LocalDateTime now = LocalDateTime.now();

        List<NotificationScheduledResponse> scheduledNotifications = queryDslNotificationRepository
            .getScheduledNotifications(now);

        // 분 간격에 따라 알림 grouping
        Map<Integer, List<NotificationScheduledResponse>> groupByInterval = scheduledNotifications
            .stream()
            .collect(Collectors.groupingBy(NotificationScheduledResponse::intervalMinutes));

        LocalTime noticeTime = now.toLocalTime();
        for (int interval : groupByInterval.keySet()) {
            List<NotificationScheduledResponse> notifications = groupByInterval.get(interval);

            if (notifications.isEmpty()) {
                continue;
            }

            // 현재 일시가 알림 일시인 경우 필터링
            List<NotificationScheduledResponse> sendNotifications = notifications.stream()
                .filter(notification -> isNoticeTime(notification, noticeTime))
                .toList();

            // 알림 이벤트 publishing
            sendNotifications
                .stream()
                .map(notification -> new NotificationSendEvent(notification.receiverToken(),
                    notification.talkTopicTitle(),
                    notification.talkTopicSubtitle()))
                .forEach(eventPublisher::publishEvent);
        }
    }

    private boolean isNoticeTime(NotificationScheduledResponse notification, LocalTime noticeTime) {
        LocalTime startTime = notification.startTime();
        int intervalMinutes = notification.intervalMinutes();
        long minutesSinceStart = ChronoUnit.MINUTES.between(startTime, noticeTime);

        return minutesSinceStart % intervalMinutes == 0;
    }
}
