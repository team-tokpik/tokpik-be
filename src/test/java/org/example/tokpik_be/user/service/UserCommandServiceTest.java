package org.example.tokpik_be.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.UserException;
import org.example.tokpik_be.support.ServiceTestSupport;
import org.example.tokpik_be.type.domain.PlaceType;
import org.example.tokpik_be.type.domain.TopicType;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.dto.request.UserMakeProfileRequest;
import org.example.tokpik_be.user.dto.request.UserUpdateNotificationTokenRequest;
import org.example.tokpik_be.user.enums.Gender;
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
            topicTypeRepository,
            placeTypeRepository,
            userRepository,
            userTopicTypeRepository,
            userPlaceTypeRepository,
            userQueryService);
    }

    @Nested
    @DisplayName("프로필 생성 시 ")
    class MakeProfileTest {

        private UserMakeProfileRequest request;

        @BeforeEach
        void setUp() {
            List<TopicType> topicTypes = List.of(new TopicType("친목"),
                new TopicType("1대1"),
                new TopicType("비즈니스"));
            topicTypeRepository.saveAll(topicTypes);

            List<PlaceType> placeTypes = List.of(new PlaceType("직장"),
                new PlaceType("학교"),
                new PlaceType("카페"));
            placeTypeRepository.saveAll(placeTypes);

            List<Long> topicTagIds = topicTypes.stream().map(TopicType::getId).toList();
            List<Long> placeTagIds = placeTypes.stream().map(PlaceType::getId).toList();
            request = new UserMakeProfileRequest(
                LocalDate.now().minusYears(20),
                Gender.MALE.toBoolean(),
                topicTagIds,
                placeTagIds);
        }

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            User user = new User("mj3242@naver.com", "profilePhotoUrl");
            userRepository.save(user);

            // when
            userCommandService.makeProfile(user.getId(), request);

            // then
            List<Long> topicTagIds = topicTypeRepository.findAll().stream().map(TopicType::getId)
                .toList();
            List<Long> placeTagIds = placeTypeRepository.findAll().stream().map(PlaceType::getId)
                .toList();

            SoftAssertions.assertSoftly(softly -> {
                assertThat(user.getBirth()).as("생일 일치 확인").isEqualTo(request.birth());
                assertThat(user.getGender()).as("성별 일치 확인")
                    .isEqualTo(Gender.from(request.gender()));

                List<Long> userTopicTagIds = user.getUserTopicTypes().stream()
                    .map(userTopicTag -> userTopicTag.getTopicType().getId()).toList();
                assertThat(userTopicTagIds).as("사용자 대화 주제 분류 목록 일치 확인")
                    .containsAnyElementsOf(topicTagIds);

                List<Long> userPlaceTagIds = user.getUserPlaceTypes().stream()
                    .map(userPlaceTag -> userPlaceTag.getPlaceType().getId()).toList();
                assertThat(userPlaceTagIds).as("사용자 대화 장소 목록 일치 확인")
                    .containsAnyElementsOf(placeTagIds);
            });
        }

        @DisplayName("존재하지 않는 사용자일 경우 예외가 발생한다.")
        @Test
        void userNotFound() {
            // given
            long userId = 1L;

            // when

            // then
            assertThatThrownBy(() -> userCommandService.makeProfile(userId, request))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(UserException.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("notification token 업데이트 시 ")
    class UpdateNotificationTokenTest {

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            User user = new User("mj3242@naver.com", "profilePhotoUrl");
            userRepository.save(user);

            long userId = user.getId();
            String token = "header.payload.signature";
            UserUpdateNotificationTokenRequest request = new UserUpdateNotificationTokenRequest(
                token);

            // when
            userCommandService.updateNotificationToken(userId, request);

            // then
            assertThat(user.getNotificationToken()).isEqualTo(token);
        }

        @DisplayName("존재하지 않는 사용자일 경우 예외가 발생한다.")
        @Test
        void userNotFound() {
            // given
            long userId = 1L;
            UserUpdateNotificationTokenRequest request = new UserUpdateNotificationTokenRequest(
                "header.payload.signature");

            // when

            // then
            assertThatThrownBy(() -> userCommandService.updateNotificationToken(userId, request))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(UserException.USER_NOT_FOUND);
        }
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
