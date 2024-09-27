package org.example.tokpik_be.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.NotificationException;
import org.example.tokpik_be.notification.domain.Notification;
import org.example.tokpik_be.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;

    public Notification findById(long notificationId) {

        return notificationRepository.findById(notificationId)
            .orElseThrow(() -> new GeneralException(NotificationException.NOTIFICATION_NOT_FOUND));
    }

}
