package org.example.tokpik_be.notification.strategy.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.notification.domain.BaseNotification;
import org.example.tokpik_be.notification.domain.Notification20Min;
import org.example.tokpik_be.notification.repository.Notification20MinRepository;
import org.example.tokpik_be.notification.strategy.query.Notification20MinQueryStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Notification20MinCommandStrategy implements NotificationCommandStrategy {

    private final Notification20MinRepository notification20MinRepository;
    private final Notification20MinQueryStrategy notification20MinQueryStrategy;

    @Override
    public void deleteById(long notificationId) {
        BaseNotification notification = notification20MinQueryStrategy.findById(notificationId);
        notification.delete();
    }

    @Override
    public void deleteAllInBatch() {
        List<Notification20Min> notifications = notification20MinRepository.findAllByDeletedIsTrue();
        List<Long> notificationIds = notifications.stream().map(Notification20Min::getId).toList();
        notification20MinRepository.deleteAllByIdInBatch(notificationIds);
    }

    @Override
    public int getIntervalMinutes() {

        return 20;
    }
}
