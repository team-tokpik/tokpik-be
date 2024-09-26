package org.example.tokpik_be.notification.strategy.command;

import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.notification.repository.Notification5MinRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Notification5MinCommandStrategy implements NotificationCommandStrategy {

    private final Notification5MinRepository notification5MinRepository;

    @Override
    public void deleteById(long notificationId) {

        notification5MinRepository.deleteById(notificationId);
    }

    @Override
    public int getIntervalMinutes() {

        return 5;
    }
}
