package org.example.tokpik_be.notification.strategy.command;

import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.notification.repository.Notification15MinRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Notification15MinCommandStrategy implements NotificationCommandStrategy {

    private final Notification15MinRepository notification15MinRepository;

    @Override
    public void deleteById(long notificationId) {

        notification15MinRepository.deleteById(notificationId);
    }

    @Override
    public int getIntervalMinutes() {

        return 15;
    }
}
