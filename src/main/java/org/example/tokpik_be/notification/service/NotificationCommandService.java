package org.example.tokpik_be.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.NotificationException;
import org.example.tokpik_be.notification.domain.Notification;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationCommandService {

    private final NotificationQueryService notificationQueryService;
    private final UserQueryService userQueryService;

    public void deleteNotification(long userId, long notificationId) {
        Notification notification = notificationQueryService.findById(notificationId);
        User user = userQueryService.findById(userId);

        if (!notification.getUser().equals(user)) {
            throw new GeneralException(NotificationException.UNAUTHORIZED_NOTIFICATION_DELETION);
        }

        notification.delete();
    }
}
