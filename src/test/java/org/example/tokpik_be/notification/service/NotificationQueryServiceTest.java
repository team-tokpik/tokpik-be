package org.example.tokpik_be.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;
import org.assertj.core.api.SoftAssertions;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.NotificationException;
import org.example.tokpik_be.notification.domain.Notification;
import org.example.tokpik_be.notification.domain.NotificationTalkTopic;
import org.example.tokpik_be.notification.dto.response.NotificationDetailResponse;
import org.example.tokpik_be.notification.dto.response.NotificationsResponse;
import org.example.tokpik_be.notification.dto.response.NotificationsResponse.NotificationResponse;
import org.example.tokpik_be.notification.dto.response.NotificationsResponse.NotificationResponse.NotificationTalkTopicTypeResponse;
import org.example.tokpik_be.notification.repository.NotificationRepository;
import org.example.tokpik_be.notification.repository.QueryDslNotificationRepository;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.type.domain.TopicType;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private QueryDslNotificationRepository queryDslNotificationRepository;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private NotificationQueryService notificationQueryService;

    @DisplayName("알림 목록을 조회할 수 있다.")
    @Test
    void getNotifications() {
        // given
        List<NotificationTalkTopicTypeResponse> notificationTalkTopicTypes = List.of(
            new NotificationTalkTopicTypeResponse(1L, "친목"),
            new NotificationTalkTopicTypeResponse(2L, "비즈니스"),
            new NotificationTalkTopicTypeResponse(3L, "1:1"));

        LocalDateTime now = LocalDateTime.now();
        LocalDate noticeDate = now.toLocalDate();
        LocalTime notificationStartTime = now.toLocalTime();
        LocalTime notificationEndTime = notificationStartTime.plusHours(1);
        int notificationInterval = 10;
        String notificationName = "알림 이름";
        long notificationTopicTotal = 10;
        List<NotificationResponse> contents = LongStream.range(1, 5)
            .mapToObj(id -> new NotificationResponse(id,
                noticeDate,
                notificationStartTime,
                notificationEndTime,
                notificationInterval,
                notificationName,
                notificationTopicTotal,
                notificationTalkTopicTypes))
            .toList();

        NotificationsResponse expected = new NotificationsResponse(contents, 5L, true, false);

        long userId = 1L;
        given(queryDslNotificationRepository.getNotifications(eq(userId), any(), anyInt()))
            .willReturn(expected);

        // when
        NotificationsResponse response = notificationQueryService
            .getNotifications(userId, null);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.contents()).hasSize(expected.contents().size());
            softly.assertThat(response.contents())
                .usingRecursiveComparison()
                .isEqualTo(expected.contents());
            softly.assertThat(response.nextCursorId()).isEqualTo(expected.nextCursorId());
            softly.assertThat(response.first()).isEqualTo(expected.first());
            softly.assertThat(response.last()).isEqualTo(expected.last());
        });
    }

    @Nested
    @DisplayName("id로 알림 조회 시 ")
    class FindByIdTest {

        private final long notificationId = 1L;

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            Notification notification = Mockito.mock(Notification.class);
            given(notificationRepository.findById(notificationId))
                .willReturn(Optional.of(notification));

            // when
            Notification response = notificationQueryService.findById(notificationId);

            // then
            assertThat(response).isEqualTo(notification);
        }

        @DisplayName("존재하지 않는 알림일 경우 예외가 발생한다.")
        @Test
        void notificationNotFound() {
            // given
            given(notificationRepository.findById(notificationId))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> notificationQueryService.findById(notificationId))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(NotificationException.NOTIFICATION_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("알림 상세 내역 조회 시 ")
    class GetNotificationDetailTest {

        private final long userId = 1L;
        private final long notificationId = 1L;

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            User user = mock(User.class);
            given(userQueryService.findById(userId)).willReturn(user);

            Notification notification = mock(Notification.class);
            given(notificationRepository.findById(notificationId))
                .willReturn(Optional.of(notification));
            given(notification.getName()).willReturn("알림 이름");
            given(notification.getUser()).willReturn(user);

            LocalTime notificationStartTime = LocalTime.of(9, 0);
            LocalTime notificationEndTime = notificationStartTime.plusHours(1);
            int notificationIntervalMinutes = 10;
            given(notification.getStartTime()).willReturn(notificationStartTime);
            given(notification.getEndTime()).willReturn(notificationEndTime);
            given(notification.getIntervalMinutes()).willReturn(notificationIntervalMinutes);

            TopicType businessTopicType = mock(TopicType.class);
            given(businessTopicType.getId()).willReturn(1L);
            given(businessTopicType.getContent()).willReturn("비즈니스");
            TopicType socialTopicType = mock(TopicType.class);
            given(socialTopicType.getId()).willReturn(2L);
            given(socialTopicType.getContent()).willReturn("친목");

            TalkTopic businessTalkTopic = mock(TalkTopic.class);
            given(businessTalkTopic.getTitle()).willReturn("비즈니스 대화 주제");
            given(businessTalkTopic.getTopicType()).willReturn(businessTopicType);
            TalkTopic socialTalkTopic = mock(TalkTopic.class);
            given(socialTalkTopic.getTitle()).willReturn("친목 대화 주제");
            given(socialTalkTopic.getTopicType()).willReturn(socialTopicType);

            NotificationTalkTopic firstNotificationTalkTopic = mock(NotificationTalkTopic.class);
            given(firstNotificationTalkTopic.getTalkTopic()).willReturn(businessTalkTopic);
            NotificationTalkTopic secondNotificationTalkTopic = mock(NotificationTalkTopic.class);
            given(secondNotificationTalkTopic.getTalkTopic()).willReturn(socialTalkTopic);

            List<NotificationTalkTopic> notificationTalkTopics = List.of(
                firstNotificationTalkTopic,
                secondNotificationTalkTopic);
            given(notification.getNotificationTalkTopics()).willReturn(notificationTalkTopics);

            // when
            NotificationDetailResponse response = notificationQueryService
                .getNotificationDetail(userId, notificationId);

            // then
            SoftAssertions.assertSoftly(softly -> {
                assertThat(response.notificationName()).isEqualTo(notification.getName());
                assertThat(response.notificationStartTime()).isEqualTo(notification.getStartTime());
                assertThat(response.notificationEndTime()).isEqualTo(notification.getEndTime());
                assertThat(response.notificationIntervalMinutes())
                    .isEqualTo(notification.getIntervalMinutes());

                assertThat(response.notificationTalkTopics()).hasSize(6)
                    .extracting("talkTopicTitle",
                        "talkTopicTypeId",
                        "talkTopicContent")
                    .contains(tuple(businessTalkTopic.getTitle(),
                            businessTopicType.getId(),
                            businessTopicType.getContent()),
                        tuple(socialTalkTopic.getTitle(),
                            socialTopicType.getId(),
                            socialTopicType.getContent())
                    );
            });
        }

        @DisplayName("존재하지 않는 알림일 경우 예외가 발생한다.")
        @Test
        void notificationNotFound() {
            // given
            User user = mock(User.class);
            given(userQueryService.findById(userId)).willReturn(user);

            given(notificationRepository.findById(notificationId))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> notificationQueryService
                .getNotificationDetail(userId, notificationId))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(NotificationException.NOTIFICATION_NOT_FOUND);
        }

        @DisplayName("자신의 알림이 아닌 경우 예외가 발생한다.")
        @Test
        void unauthorizedNotificationAccess() {
            // given
            User user = mock(User.class);
            given(userQueryService.findById(userId)).willReturn(user);

            Notification notification = mock(Notification.class);
            given(notificationRepository.findById(notificationId))
                .willReturn(Optional.of(notification));

            User otherUser = mock(User.class);
            given(notification.getUser()).willReturn(otherUser);

            // when & then
            assertThatThrownBy(() -> notificationQueryService
                .getNotificationDetail(userId, notificationId))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(NotificationException.UNAUTHORIZED_NOTIFICATION_ACCESS);
        }
    }
}
