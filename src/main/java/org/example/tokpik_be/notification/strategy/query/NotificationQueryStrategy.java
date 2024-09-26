package org.example.tokpik_be.notification.strategy.query;

import org.example.tokpik_be.notification.domain.BaseNotification;

public interface NotificationQueryStrategy {

    BaseNotification findById(long notificationId);

    int getIntervalMinutes();
}
