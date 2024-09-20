package org.example.tokpik_be.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.UserException;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

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
            true);

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            User user = Mockito.mock(User.class);
            given(userQueryService.findById(userId)).willReturn(user);
            Gender gender = Gender.from(request.gender());

            // when
            userCommandService.makeProfile(userId, request);

            // then
            Assertions.assertAll(
                () -> verify(userQueryService).findById(userId),
                () -> verify(user).updateProfile(eq(request.birth()), eq(gender))
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