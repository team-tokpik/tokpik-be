package org.example.tokpik_be.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.UserException;
import org.example.tokpik_be.support.ServiceTestSupport;
import org.example.tokpik_be.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserCommandServiceTest extends ServiceTestSupport {

    private UserQueryService userQueryService;
    private UserCommandService userCommandService;

    @BeforeEach
    void setUp() {
        this.userQueryService = new UserQueryService(userRepository);
        this.userCommandService = new UserCommandService(
            topicTagRepository,
            placeTagRepository,
            userRepository,
            userTopicTagRepository,
            userPlaceTagRepository,
            userQueryService);
    }

    @Nested
    @DisplayName("회원 탈퇴 시 ")
    class DeleteUserTest {

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            User user = new User("ex@example.com", "http://users/profile-photos");
            userRepository.save(user);

            long userId = user.getId();

            // when
            userCommandService.deleteUser(userId);

            // then
            Optional<User> foundUser = userRepository.findById(userId);
            assertThat(foundUser).isEmpty();
        }

        @DisplayName("존재하지 않는 사용자일 경우 예외가 발생한다.")
        @Test
        void userNotFound() {
            // given
            long userId = 1L;

            // when

            // then
            assertThatThrownBy(() -> userCommandService.deleteUser(userId))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(UserException.USER_NOT_FOUND);
        }
    }
}
