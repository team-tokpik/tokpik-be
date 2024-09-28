package org.example.tokpik_be.notification.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.NotificationException;
import org.example.tokpik_be.notification.domain.Notification;
import org.example.tokpik_be.notification.dto.response.NotificationsResponse;
import org.example.tokpik_be.notification.repository.NotificationRepository;
import org.example.tokpik_be.notification.repository.QueryDslNotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;
    private final QueryDslNotificationRepository queryDslNotificationRepository;

    public Notification findById(long notificationId) {

        return notificationRepository.findById(notificationId)
            .orElseThrow(() -> new GeneralException(NotificationException.NOTIFICATION_NOT_FOUND));
    }

    public NotificationsResponse getNotifications(long userId, Long nextContentId) {
        long nextNotificationId = Optional.ofNullable(nextContentId).orElse(Long.MAX_VALUE);
        int pageSize = 10;

        return queryDslNotificationRepository
            .getNotifications(userId, nextNotificationId, pageSize);
    }
}
