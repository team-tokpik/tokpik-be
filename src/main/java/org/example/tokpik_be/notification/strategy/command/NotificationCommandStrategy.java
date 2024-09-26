package org.example.tokpik_be.notification.strategy.command;

public interface NotificationCommandStrategy {

    void deleteById(long notificationId);

    int getIntervalMinutes();
}
