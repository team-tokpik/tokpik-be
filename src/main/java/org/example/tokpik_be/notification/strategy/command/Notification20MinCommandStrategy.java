package org.example.tokpik_be.notification.strategy.command;

import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.notification.repository.Notification20MinRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Notification20MinCommandStrategy implements NotificationCommandStrategy {

    private final Notification20MinRepository notification20MinRepository;

    @Override
    public void deleteById(long notificationId) {

        notification20MinRepository.deleteById(notificationId);
    }

    @Override
    public int getIntervalMinutes() {

        return 20;
    }
}
