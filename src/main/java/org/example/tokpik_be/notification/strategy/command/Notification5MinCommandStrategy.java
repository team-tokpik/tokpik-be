package org.example.tokpik_be.notification.strategy.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.notification.domain.BaseNotification;
import org.example.tokpik_be.notification.domain.Notification5Min;
import org.example.tokpik_be.notification.repository.Notification5MinRepository;
import org.example.tokpik_be.notification.strategy.query.Notification5MinQueryStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Notification5MinCommandStrategy implements NotificationCommandStrategy {

    private final Notification5MinRepository notification5MinRepository;
    private final Notification5MinQueryStrategy notification5MinQueryStrategy;

    @Override
    public void deleteById(long notificationId) {
        BaseNotification notification = notification5MinQueryStrategy.findById(notificationId);
        notification.delete();
    }

    @Override
    public void deleteAllInBatch() {
        List<Notification5Min> notifications = notification5MinRepository.findAllByDeletedIsTrue();
        List<Long> notificationIds = notifications.stream().map(Notification5Min::getId).toList();
        notification5MinRepository.deleteAllByIdInBatch(notificationIds);
    }

    @Override
    public int getIntervalMinutes() {

        return 5;
    }
}
