package org.example.tokpik_be.notification.strategy.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.notification.domain.BaseNotification;
import org.example.tokpik_be.notification.domain.Notification10Min;
import org.example.tokpik_be.notification.repository.Notification10MinRepository;
import org.example.tokpik_be.notification.strategy.query.Notification10MinQueryStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Notification10MinCommandStrategy implements NotificationCommandStrategy {

    private final Notification10MinRepository notification10MinRepository;
    private final Notification10MinQueryStrategy notification10MinQueryStrategy;

    @Override
    public void deleteById(long notificationId) {
        BaseNotification notification = notification10MinQueryStrategy.findById(notificationId);
        notification.delete();
    }

    @Override
    public void deleteAllInBatch() {
        List<Notification10Min> notifications = notification10MinRepository.findAllByDeletedIsTrue();
        List<Long> notificationIds = notifications.stream().map(Notification10Min::getId).toList();
        notification10MinRepository.deleteAllByIdInBatch(notificationIds);
    }

    @Override
    public int getIntervalMinutes() {

        return 10;
    }
}
