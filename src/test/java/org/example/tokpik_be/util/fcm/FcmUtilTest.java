package org.example.tokpik_be.util.fcm;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.example.tokpik_be.util.fcm.dto.FcmSendNotificationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

@ExtendWith(MockitoExtension.class)
class FcmUtilTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @InjectMocks
    private FcmUtil fcmUtil;

    @Nested
    @DisplayName("fcm을 통해 알림 전송 시 ")
    class SendNotificationTest {
        @DisplayName("성공한다.")
        @Test
        void success() throws FirebaseMessagingException {
            // given
            FcmSendNotificationRequest request = new FcmSendNotificationRequest("header.payload.signature",
                "알림 제목",
                "알림 내용");

            given(firebaseMessaging.send(any(Message.class))).willReturn(any(String.class));

            // when
            fcmUtil.sendNotification(request);

            // then
            verify(firebaseMessaging).send(any(Message.class));
        }

        @DisplayName("문제가 있을 경우 예외가 발생한다.")
        @Test
        void firebaseMessagingException() throws FirebaseMessagingException {
            // given
            FcmSendNotificationRequest request = new FcmSendNotificationRequest("header.payload.signature",
                "알림 제목",
                "알림 내용");

            doThrow(new RuntimeException("exception")).when(firebaseMessaging).send(any(Message.class));

            // when & then
            assertThatThrownBy(() -> fcmUtil.sendNotification(request))
                .isInstanceOf(IllegalStateException.class)
                .extracting("message")
                .isEqualTo("firebase message 전송 중 예외 발생 : exception");
        }
    }
}
