package org.example.tokpik_be.user.service;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.UserException;
import org.example.tokpik_be.support.ServiceTestSupport;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.dto.response.UserProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserQueryServiceTest extends ServiceTestSupport {

    private UserQueryService userQueryService;

    @BeforeEach
    void setUp() {
        this.userQueryService = new UserQueryService(userRepository);
    }

    @Nested
    @DisplayName("마이 페이지 프로필 조회시 ")
    class GetUserProfileTest {

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            User user = new User("ex@example.com", "http://users/profile-photos");
            userRepository.save(user);

            long userId = user.getId();

            // when
            UserProfileResponse response = userQueryService.getUserProfile(userId);

            // then
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(response.maskedEmail()).isEqualTo("e*@example.com");
                softly.assertThat(response.profilePhotoUrl()).isEqualTo(user.getProfilePhotoUrl());
            });
        }

        @DisplayName("존재하지 않은 사용자일 경우 예외가 발생한다.")
        @Test
        void userNotFound() {
            // given
            long userId = 1L;

            // when

            // then
            Assertions.assertThatThrownBy(() -> userQueryService.getUserProfile(userId))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(UserException.USER_NOT_FOUND);
        }
    }
}
