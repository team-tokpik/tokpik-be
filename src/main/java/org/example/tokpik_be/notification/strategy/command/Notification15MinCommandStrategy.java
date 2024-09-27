package org.example.tokpik_be.notification.strategy.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.notification.domain.BaseNotification;
import org.example.tokpik_be.notification.domain.Notification15Min;
import org.example.tokpik_be.notification.repository.Notification15MinRepository;
import org.example.tokpik_be.notification.strategy.query.Notification15MinQueryStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Notification15MinCommandStrategy implements NotificationCommandStrategy {

    private final Notification15MinRepository notification15MinRepository;
    private final Notification15MinQueryStrategy notification15MinQueryStrategy;

    @Override
    public void deleteById(long notificationId) {
        BaseNotification notification = notification15MinQueryStrategy.findById(notificationId);
        notification.delete();
    }

    @Override
    public void deleteAllInBatch() {
        List<Notification15Min> notifications = notification15MinRepository.findAllByDeletedIsTrue();
        List<Long> notificationIds = notifications.stream().map(Notification15Min::getId).toList();
        notification15MinRepository.deleteAllByIdInBatch(notificationIds);
    }

    @Override
    public int getIntervalMinutes() {

        return 15;
    }
}
