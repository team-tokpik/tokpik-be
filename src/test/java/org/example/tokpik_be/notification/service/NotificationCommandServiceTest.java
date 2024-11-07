package org.example.tokpik_be.notification.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.NotificationException;
import org.example.tokpik_be.exception.ScrapException;
import org.example.tokpik_be.notification.domain.Notification;
import org.example.tokpik_be.notification.dto.request.NotificationCreateRequest;
import org.example.tokpik_be.notification.repository.NotificationRepository;
import org.example.tokpik_be.notification.repository.NotificationTalkTopicRepository;
import org.example.tokpik_be.scrap.domain.Scrap;
import org.example.tokpik_be.scrap.domain.ScrapTopic;
import org.example.tokpik_be.scrap.repository.ScrapRepository;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.talk_topic.repository.TalkTopicRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationCommandServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationTalkTopicRepository notificationTalkTopicRepository;

    @Mock
    private ScrapRepository scrapRepository;

    @Mock
    private TalkTopicRepository talkTopicRepository;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private NotificationQueryService notificationQueryService;

    @InjectMocks
    private NotificationCommandService notificationCommandService;

    @Nested
    @DisplayName("알림 삭제 시 ")
    class DeleteNotificationTest {

        private final long userId = 1L;
        private final long notificationId = 1L;

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            Notification notification = Mockito.mock(Notification.class);
            given(notificationQueryService.findById(notificationId))
                .willReturn(notification);

            User user = Mockito.mock(User.class);
            given(userQueryService.findById(userId)).willReturn(user);

            given(notification.getUser()).willReturn(user);

            // when
            notificationCommandService.deleteNotification(userId, notificationId);

            // then
            verify(notification, times(1)).delete();
        }

        @DisplayName("자신의 알림만 삭제할 수 있다.")
        @Test
        void unauthorizedNotificationDeletion() {
            // given
            Notification notification = Mockito.mock(Notification.class);
            given(notificationQueryService.findById(notificationId))
                .willReturn(notification);

            User user = Mockito.mock(User.class);
            given(userQueryService.findById(userId)).willReturn(user);

            User otherUser = Mockito.mock(User.class);
            given(notification.getUser()).willReturn(otherUser);

            // when & then
            assertThatThrownBy(() -> notificationCommandService
                .deleteNotification(userId, notificationId))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(NotificationException.UNAUTHORIZED_NOTIFICATION_DELETION);
        }
    }

    @Nested
    @DisplayName("알림 생성 시 ")
    class CreateNotificationTest {

        private final long userId = 1L;
        private User user = mock(User.class);
        private NotificationCreateRequest request;

        @BeforeEach
        void setUp() {
            LocalDateTime now = LocalDateTime.now();
            LocalTime notificationStartTime = now.toLocalTime();
            this.request = new NotificationCreateRequest(
                "알림 이름",
                1L,
                List.of(1L),
                now.toLocalDate(),
                notificationStartTime,
                notificationStartTime.plusHours(1),
                10);
        }

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            given(userQueryService.findById(userId)).willReturn(user);

            Scrap scrap = mock(Scrap.class);
            given(scrapRepository.findById(request.scrapId())).willReturn(Optional.of(scrap));

            given(scrap.getUser()).willReturn(user);

            ScrapTopic scrapTopic = mock(ScrapTopic.class);
            TalkTopic talkTopic = mock(TalkTopic.class);
            given(scrap.getScrapTopics()).willReturn(List.of(scrapTopic));
            given(scrapTopic.getTalkTopic()).willReturn(talkTopic);
            given(talkTopic.getId()).willReturn(request.notificationTalkTopicIds().get(0));

            given(talkTopicRepository.findAllById(request.notificationTalkTopicIds()))
                .willReturn(List.of(talkTopic));

            long notificationId = 1L;
            Notification savedNotification = mock(Notification.class);
            given(notificationRepository.save(any(Notification.class))).willReturn(
                savedNotification);
            given(savedNotification.getId()).willReturn(notificationId);

            doNothing().when(savedNotification).addNotificationTalkTopics(anyList());

            // when
            notificationCommandService.createNotification(userId, request);

            // then
            Assertions.assertAll(() -> verify(userQueryService).findById(userId),
                () -> verify(scrapRepository).findById(request.scrapId()),
                () -> verify(notificationRepository).save(any(Notification.class)),
                () -> verify(talkTopicRepository).findAllById(anyList()),
                () -> verify(notificationTalkTopicRepository).saveAll(anyList()));
        }

        @DisplayName("스크랩이 존재하지 않으면 예외가 발생한다.")
        @Test
        void scrapNotFound() {
            // given

            // when & then
            assertThatThrownBy(() -> notificationCommandService.createNotification(userId, request))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(ScrapException.SCRAP_NOT_FOUND);
        }

        @DisplayName("요청한 사용자의 스크랩이 아니면 예외가 발생한다.")
        @Test
        void unauthorizedScrapAccess() {
            // given
            given(userQueryService.findById(userId)).willReturn(user);

            Scrap scrap = mock(Scrap.class);
            given(scrapRepository.findById(request.scrapId())).willReturn(Optional.of(scrap));

            User otherUser = mock(User.class);
            given(scrap.getUser()).willReturn(otherUser);

            // when & then
            assertThatThrownBy(() -> notificationCommandService.createNotification(userId, request))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(ScrapException.UNAUTHORIZED_SCRAP_ACCESS);
        }

        @DisplayName("요청된 알림 간격이 5,10,15,20분 중 하나에 속하지 않을 경우 예외가 발생한다.")
        @Test
        void invalidNotificationInterval() {
            // given
            given(userQueryService.findById(userId)).willReturn(user);

            Scrap scrap = mock(Scrap.class);
            given(scrapRepository.findById(request.scrapId())).willReturn(Optional.of(scrap));

            given(scrap.getUser()).willReturn(user);

            int invalidNotificationIntervalMinutes = 30;
            NotificationCreateRequest invalidRequest = new NotificationCreateRequest(
                request.notificationName(),
                request.scrapId(),
                request.notificationTalkTopicIds(),
                request.noticeDate(),
                request.notificationStartTime(),
                request.notificationEndTime(),
                invalidNotificationIntervalMinutes);

            // when & then
            assertThatThrownBy(() -> notificationCommandService
                .createNotification(userId, invalidRequest))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(NotificationException.INVALID_NOTIFICATION_INTERVAL);
        }
    }
}