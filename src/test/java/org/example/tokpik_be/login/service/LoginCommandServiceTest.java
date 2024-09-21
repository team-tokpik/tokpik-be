package org.example.tokpik_be.login.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.LoginException;
import org.example.tokpik_be.exception.UserException;
import org.example.tokpik_be.login.dto.request.AccessTokenRefreshRequest;
import org.example.tokpik_be.login.dto.response.AccessTokenRefreshResponse;
import org.example.tokpik_be.user.service.UserQueryService;
import org.example.tokpik_be.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginCommandServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserQueryService userQueryService;

    @InjectMocks
    private LoginCommandService loginCommandService;

    @Nested
    @DisplayName("access token refresh 시 ")
    class RefreshAccessTokenTest {

        private final String jwt = "header.payload.signature";

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            long userId = 1L;
            AccessTokenRefreshRequest request = new AccessTokenRefreshRequest(jwt);

            given(jwtUtil.parseUserIdFromToken(request.refreshToken())).willReturn(userId);
            given(userQueryService.notExistsById(userId)).willReturn(false);
            given(jwtUtil.generateAccessToken(userId)).willReturn(jwt);

            // when
            AccessTokenRefreshResponse response = loginCommandService.refreshAccessToken(request);

            // then
            assertThat(response.accessToken()).isEqualTo(jwt);
        }

        @DisplayName("access token이 유효하지 않을 경우 예외가 발생한다.")
        @Test
        void invalidAccessToken() {
            // given
            String invalidJwt = "invalidJwt";
            AccessTokenRefreshRequest request = new AccessTokenRefreshRequest(invalidJwt);

            given(jwtUtil.parseUserIdFromToken(request.refreshToken()))
                .willThrow(new GeneralException(LoginException.INVALID_JWT));

            // when

            // then
            assertThatThrownBy(() -> loginCommandService.refreshAccessToken(request))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(LoginException.INVALID_JWT);
        }

        @DisplayName("사용자가 존재하지 않을 경우 예외가 발생한다.")
        @Test
        void userNotFound() {
            // given
            long userId = 1L;
            AccessTokenRefreshRequest request = new AccessTokenRefreshRequest(jwt);

            given(jwtUtil.parseUserIdFromToken(request.refreshToken())).willReturn(userId);
            given(userQueryService.notExistsById(userId)).willReturn(true);

            // when

            // then
            assertThatThrownBy(() -> loginCommandService.refreshAccessToken(request))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(UserException.USER_NOT_FOUND);
        }
    }
}
