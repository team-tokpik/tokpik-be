package org.example.tokpik_be.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.notification.domain.BaseNotification;
import org.example.tokpik_be.notification.strategy.query.NotificationQueryStrategyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationQueryStrategyFactory notificationQueryStrategyFactory;

    public BaseNotification findByIdAndIntervalMinutes(long notificationId, int intervalMinutes) {

        return notificationQueryStrategyFactory.findByIdAndIntervalMinutes(notificationId,
            intervalMinutes);
    }
}
