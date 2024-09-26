package org.example.tokpik_be.notification.strategy.query;

import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.NotificationException;
import org.example.tokpik_be.notification.domain.BaseNotification;
import org.example.tokpik_be.notification.repository.Notification5MinRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Notification5MinQueryStrategy implements NotificationQueryStrategy {

    private final Notification5MinRepository notification5MinRepository;

    @Override
    public BaseNotification findById(long notificationId) {

        return notification5MinRepository.findById(notificationId)
            .orElseThrow(() -> new GeneralException(NotificationException.NOTIFICATION_NOT_FOUND));
    }

    @Override
    public int getIntervalMinutes() {

        return 5;
    }
}
