package org.example.tokpik_be.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.notification.dto.request.NotificationDeleteRequest;
import org.example.tokpik_be.notification.repository.NotificationTopicRepository;
import org.example.tokpik_be.notification.strategy.command.NotificationCommandStrategyFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationCommandService {

    private final NotificationTopicRepository notificationTopicRepository;
    private final NotificationCommandStrategyFactory notificationCommandStrategyFactory;

    public void deleteNotification(NotificationDeleteRequest request) {

        notificationCommandStrategyFactory.deleteByIdAndIntervalMinutes(request.notificationId(),
                request.notificationIntervalMinutes());
    }

    @Scheduled(cron = "0 0 0 L 3,6,9,12 ?")
    public void deleteNotifications() {
        notificationCommandStrategyFactory.deleteNotificationsInBatch();
    }
}
