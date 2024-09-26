package org.example.tokpik_be.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.notification.dto.request.NotificationDeleteRequest;
import org.example.tokpik_be.notification.strategy.command.NotificationCommandStrategyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationCommandService {

    private final NotificationCommandStrategyFactory notificationCommandStrategyFactory;

    public void deleteNotification(NotificationDeleteRequest request) {

        notificationCommandStrategyFactory
            .deleteByIdAndIntervalMinutes(request.notificationId(),
                request.notificationIntervalMinutes());
    }
}
