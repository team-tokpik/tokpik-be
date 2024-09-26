package org.example.tokpik_be.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.UserException;
import org.example.tokpik_be.tag.domain.PlaceTag;
import org.example.tokpik_be.tag.domain.TopicTag;
import org.example.tokpik_be.tag.repository.PlaceTagRepository;
import org.example.tokpik_be.tag.repository.TopicTagRepository;
import org.example.tokpik_be.tag.repository.UserPlaceTagRepository;
import org.example.tokpik_be.tag.repository.UserTopicTagRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.dto.request.UserMakeProfileRequest;
import org.example.tokpik_be.user.enums.Gender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

    @Mock
    private TopicTagRepository topicTagRepository;

    @Mock
    private PlaceTagRepository placeTagRepository;

    @Mock
    private UserTopicTagRepository userTopicTagRepository;

    @Mock
    private UserPlaceTagRepository userPlaceTagRepository;

    @Mock
    private UserQueryService userQueryService;

    @InjectMocks
    private UserCommandService userCommandService;

    @Nested
    @DisplayName("프로필 생성 시 ")
    class MakeProfileTest {

        private final long userId = 1L;
        private final UserMakeProfileRequest request = new UserMakeProfileRequest(
            LocalDate.now().minusYears(20),
            true,
            List.of(1L, 2L, 3L),
            List.of(1L, 2L, 3L)
        );

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            User user = mock(User.class);
            given(user.getId()).willReturn(userId);
            given(userQueryService.findById(userId)).willReturn(user);
            Gender gender = Gender.from(request.gender());

            doNothing().when(userTopicTagRepository).deleteByUserId(eq(userId));

            TopicTag topicTag = mock(TopicTag.class);
            List<TopicTag> topicTags = List.of(topicTag);
            given(topicTagRepository.findAllById(eq(request.topicTagIds())))
                .willReturn(topicTags);

            doNothing().when(userPlaceTagRepository).deleteByUserId(eq(userId));

            PlaceTag placeTag = mock(PlaceTag.class);
            List<PlaceTag> placeTags = List.of(placeTag);
            given(placeTagRepository.findAllById(eq(request.placeTagIds())))
                .willReturn(placeTags);

            // when
            userCommandService.makeProfile(userId, request);

            // then
            Assertions.assertAll(
                () -> verify(userQueryService).findById(userId),
                () -> verify(user).updateProfile(eq(request.birth()), eq(gender)),
                () -> verify(user).updateUserTopicTags(anyList()),
                () -> verify(user).updateUserPlaceTags(anyList())
            );
        }

        @DisplayName("사용자가 존재하지 않으면 예외가 발생한다.")
        @Test
        void userNotFound() {
            // given
            given(userQueryService.findById(userId))
                .willThrow(new GeneralException(UserException.USER_NOT_FOUND));

            // when

            // then
            assertThatThrownBy(() -> userCommandService.makeProfile(userId, request))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(UserException.USER_NOT_FOUND);
        }
    }
}