package org.example.tokpik_be.notification.strategy.command;

import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.notification.repository.Notification10MinRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Notification10MinCommandStrategy implements NotificationCommandStrategy {

    private final Notification10MinRepository notification10MinRepository;

    @Override
    public void deleteById(long notificationId) {

        notification10MinRepository.deleteById(notificationId);
    }

    @Override
    public int getIntervalMinutes() {

        return 10;
    }
}
