package org.example.tokpik_be.notification.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.NotificationException;
import org.example.tokpik_be.notification.domain.Notification;
import org.example.tokpik_be.notification.domain.NotificationTalkTopic;
import org.example.tokpik_be.notification.repository.NotificationRepository;
import org.example.tokpik_be.notification.repository.NotificationTalkTopicRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationCommandService {

    private final NotificationRepository notificationRepository;
    private final NotificationTalkTopicRepository notificationTalkTopicRepository;

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
}
