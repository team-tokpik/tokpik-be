package org.example.tokpik_be.util.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.util.fcm.dto.FcmSendNotificationRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FcmUtil {

    private final FirebaseMessaging firebaseMessaging;

    @Async
    public void sendNotification(FcmSendNotificationRequest request) {
        WebpushConfig webpushConfig = WebpushConfig.builder()
            .setNotification(WebpushNotification.builder()
                .setTitle(request.title())
                .setBody(request.content())
                .build())
            .build();

        Message message = Message.builder()
            .setToken(request.receiverToken())
            .setWebpushConfig(webpushConfig)
            .build();

        try {
            firebaseMessaging.send(message);
        } catch (Exception e) {
            String exceptionMessage = "firebase message 전송 중 예외 발생 : %s".formatted(e.getMessage());
            throw new IllegalStateException(exceptionMessage);
        }
    }
}
