package org.example.tokpik_be.login.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Date;

import org.assertj.core.api.SoftAssertions;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.LoginException;
import org.example.tokpik_be.exception.UserException;
import org.example.tokpik_be.login.dto.request.AccessTokenRefreshRequest;
import org.example.tokpik_be.login.dto.request.LoginByKakaoRequest;
import org.example.tokpik_be.login.dto.response.AccessTokenRefreshResponse;
import org.example.tokpik_be.login.dto.response.KakaoUserResponse;
import org.example.tokpik_be.login.dto.response.LoginResponse;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserCommandService;
import org.example.tokpik_be.user.service.UserQueryService;
import org.example.tokpik_be.util.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginCommandServiceTest {

    private final String JWT = "header.payload.signature";

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserCommandService userCommandService;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private KakaoApiClient kakaoApiClient;

    @InjectMocks
    private LoginCommandService loginCommandService;

    @Nested
    @DisplayName("카카오 소셜 로그인 시 ")
    class KakaoLoginTest {

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            LoginByKakaoRequest request = new LoginByKakaoRequest("code");

            KakaoUserResponse kakaoUserResponse = new KakaoUserResponse("ex@example.com",
                "https://kakao.com/profile-photo/1");
            given(kakaoApiClient.requestKakaoUser(request.code())).willReturn(kakaoUserResponse);

            long userId = 1L;
            User user = mock(User.class);
            given(user.getId()).willReturn(userId);
            given(user.getEmail()).willReturn(kakaoUserResponse.email());
            given(user.requiresProfile()).willReturn(false);

            given(userQueryService.notExistByEmail(user.getEmail())).willReturn(false);
            given(userQueryService.findByEmail(user.getEmail())).willReturn(user);

            given(jwtUtil.generateAccessToken(eq(userId), any(Date.class))).willReturn(JWT);
            given(jwtUtil.generateRefreshToken(eq(userId), any(Date.class))).willReturn(JWT);

            // when
            LoginResponse response = loginCommandService.kakaoLogin(request);

            // then
            Assertions.assertAll(() -> assertThat(response.requiresProfile()).isFalse(),
                () -> assertThat(response.accessToken()).isEqualTo(JWT),
                () -> assertThat(response.refreshToken()).isEqualTo(JWT),
                () -> verify(user, times(1)).updateRefreshToken(JWT)
            );
        }

        @DisplayName("신규 가입 사용자일 경우 사용자 정보를 저장한다.")
        @Test
        void saveUserWhenJoin() {
            // given
            LoginByKakaoRequest request = new LoginByKakaoRequest("code");

            KakaoUserResponse kakaoUserResponse = new KakaoUserResponse("ex@example.com",
                "https://kakao.com/profile-photo/1");
            given(kakaoApiClient.requestKakaoUser(request.code())).willReturn(kakaoUserResponse);

            long userId = 1L;
            User user = mock(User.class);
            given(user.getId()).willReturn(userId);
            given(user.getEmail()).willReturn(kakaoUserResponse.email());
            given(user.requiresProfile()).willReturn(true);

            given(userQueryService.notExistByEmail(user.getEmail())).willReturn(true);
            given(userQueryService.findByEmail(user.getEmail())).willReturn(user);

            given(jwtUtil.generateAccessToken(eq(userId), any(Date.class))).willReturn(JWT);
            given(jwtUtil.generateRefreshToken(eq(userId), any(Date.class))).willReturn(JWT);

            // when
            LoginResponse response = loginCommandService.kakaoLogin(request);

            // then
            Assertions.assertAll(() -> assertThat(response.requiresProfile()).isTrue(),
                () -> verify(userCommandService, times(1)).save(any(User.class))
            );
        }
    }

    @Nested
    @DisplayName("access token refresh 시 ")
    class RefreshAccessTokenTest {

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            AccessTokenRefreshRequest request = new AccessTokenRefreshRequest(JWT);

            long userId = 1L;
            User user = mock(User.class);
            given(user.notEqualRefreshToken(request.refreshToken())).willReturn(false);

            given(jwtUtil.parseUserIdFromToken(request.refreshToken())).willReturn(userId);
            given(userQueryService.findById(userId)).willReturn(user);
            given(jwtUtil.generateAccessToken(eq(userId), any(Date.class))).willReturn(JWT);
            given(jwtUtil.generateRefreshToken(eq(userId), any(Date.class))).willReturn(JWT);

            // when
            AccessTokenRefreshResponse response = loginCommandService.refreshAccessToken(request);

            // then
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(response.accessToken()).isEqualTo(JWT);
                softly.assertThat(response.refreshToken()).isEqualTo(JWT);
            });
        }

        @DisplayName("access token이 유효하지 않을 경우 예외가 발생한다.")
        @Test
        void invalidAccessToken() {
            // given
            String invalidJwt = "invalidJwt";
            AccessTokenRefreshRequest request = new AccessTokenRefreshRequest(invalidJwt);

            given(jwtUtil.parseUserIdFromToken(request.refreshToken()))
                .willThrow(new GeneralException(LoginException.INVALID_JWT));

            // when & then
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
            AccessTokenRefreshRequest request = new AccessTokenRefreshRequest(JWT);

            given(jwtUtil.parseUserIdFromToken(request.refreshToken())).willReturn(userId);
            given(userQueryService.findById(userId))
                .willThrow(new GeneralException(UserException.USER_NOT_FOUND));

            // when & then
            assertThatThrownBy(() -> loginCommandService.refreshAccessToken(request))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(UserException.USER_NOT_FOUND);
        }

        @DisplayName("refresh token이 일치하지 않을 경우 예외가 발생한다.")
        @Test
        void refreshTokenNotEqual() {
            // given
            String refreshToken = "refreshToken";
            AccessTokenRefreshRequest request = new AccessTokenRefreshRequest(refreshToken);

            long userId = 1L;
            given(jwtUtil.parseUserIdFromToken(request.refreshToken())).willReturn(userId);

            User user = mock(User.class);
            given(userQueryService.findById(userId)).willReturn(user);
            given(user.notEqualRefreshToken(refreshToken)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> loginCommandService.refreshAccessToken(request))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(LoginException.INVALID_JWT);
        }
    }
}
