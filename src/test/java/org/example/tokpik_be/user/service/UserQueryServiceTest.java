package org.example.tokpik_be.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
            User user = new User("ex@example.com", "profilePhotoUrl");
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
            assertThatThrownBy(() -> userQueryService.getUserProfile(userId))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(UserException.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("ID로 사용자로 조회 시 ")
    class FindByIdTest {

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            User user = new User("ex@example.com", "profilePhotoUrl");
            userRepository.save(user);

            // when
            User result = userQueryService.findById(user.getId());

            // then
            assertThat(result).isEqualTo(user);
        }

        @DisplayName("존재하지 않는 사용자일 경우 예외가 발생한다.")
        @Test
        void userNotFound() {
            // given
            long userId = 1L;

            // when

            // then
            assertThatThrownBy(() -> userQueryService.findById(userId))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(UserException.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("이메일로 사용자 조회 시 ")
    class FindByEmailTest {

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            User user = new User("ex@example.com", "profilePhotoUrl");
            userRepository.save(user);

            // when
            User result = userQueryService.findByEmail(user.getEmail());

            // then
            assertThat(result).isEqualTo(user);
        }

        @DisplayName("존재하지 않는 사용자일 경우 예외가 발생한다.")
        @Test
        void userNotFound() {
            // given
            String email = "ex@example.com";

            // when

            // then
            assertThatThrownBy(() -> userQueryService.findByEmail(email))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(UserException.USER_NOT_FOUND);
        }
    }
}
