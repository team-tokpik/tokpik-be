package org.example.tokpik_be.notification.event;

import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.util.fcm.FcmUtil;
import org.example.tokpik_be.util.fcm.dto.FcmSendNotificationRequest;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final FcmUtil fcmUtil;

    @EventListener
    public void sendNotification(NotificationSendEvent sendEvent) {
        FcmSendNotificationRequest request = FcmSendNotificationRequest.from(sendEvent);
        fcmUtil.sendNotification(request);
    }
}
