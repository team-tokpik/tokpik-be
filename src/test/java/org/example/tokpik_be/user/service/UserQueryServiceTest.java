package org.example.tokpik_be.user.service;

import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.UserException;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.dto.response.UserProfileResponse;
import org.example.tokpik_be.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserQueryService userQueryService;

    @Nested
    @DisplayName("마이 페이지 프로필 조회 시 ")
    class GetUserProfileTest {

        private final long userId = 1L;

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            User user = Mockito.mock(User.class);
            given(user.getEmail()).willReturn("mj3242@naver.com");
            given(user.getProfilePhotoUrl()).willReturn("profile-photo/1");

            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // when
            UserProfileResponse response = userQueryService.getUserProfile(userId);

            // then
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(response.maskedEmail()).isEqualTo("m*****@naver.com");
                softly.assertThat(response.profilePhotoUrl()).isEqualTo(user.getProfilePhotoUrl());
            });
        }

        @DisplayName("사용자가 존재하지 않으면 예외가 발생한다.")
        @Test
        void userNotFound() {
            // given
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when

            // then
            Assertions.assertThatThrownBy(() -> userQueryService.findById(userId))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(UserException.USER_NOT_FOUND);
        }
    }
}
