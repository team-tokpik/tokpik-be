package org.example.tokpik_be.login.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.tokpik_be.login.dto.request.AccessTokenRefreshRequest;
import org.example.tokpik_be.login.dto.request.LoginByKakaoRequest;
import org.example.tokpik_be.login.dto.response.AccessTokenRefreshResponse;
import org.example.tokpik_be.login.dto.response.LoginResponse;
import org.example.tokpik_be.login.service.LoginCommandService;
import org.example.tokpik_be.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class LoginControllerTest extends ControllerTestSupport {

    @Mock
    private LoginCommandService loginCommandService;

    @InjectMocks
    private LoginController loginController;

    @Override
    protected Object initController() {
        return loginController;
    }

    @Nested
    @DisplayName("카카오 소셜 로그인 시 ")
    class KakaoLoginTest {

        @DisplayName("성공한다.")
        @Test
        void success() throws Exception {
            // given
            LoginByKakaoRequest request = new LoginByKakaoRequest("code");

            String jwt = "header.payload.signature";
            LoginResponse response = new LoginResponse(true, jwt, jwt);
            given(loginCommandService.kakaoLogin(request)).willReturn(response);

            // when
            ResultActions resultActions = mockMvc.perform(post("/login/kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.requiresProfile").value(response.requiresProfile()))
                .andExpect(jsonPath("$.accessToken").value(response.accessToken()))
                .andExpect(jsonPath("$.refreshToken").value(response.refreshToken()));
        }

        @DisplayName("유효하지 않은 인가 코드일 경우 예외가 발생한다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void invalidCode(String code) throws Exception {
            // given
            LoginByKakaoRequest request = new LoginByKakaoRequest(code);

            // when
            ResultActions resultActions = mockMvc.perform(post("/login/kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.code").value("인가 코드는 필수 값"));
        }
    }

    @Nested
    @DisplayName("access token 갱신 시 ")
    class RefreshAccessTokenTest {

        @DisplayName("성공한다.")
        @Test
        void success() throws Exception {
            // given
            String jwt = "header.payload.signature";
            AccessTokenRefreshRequest request = new AccessTokenRefreshRequest(jwt);

            AccessTokenRefreshResponse response = new AccessTokenRefreshResponse(jwt, jwt);
            given(loginCommandService.refreshAccessToken(request)).willReturn(response);

            // when
            ResultActions resultActions = mockMvc.perform(post("/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(response.accessToken()))
                .andExpect(jsonPath("$.refreshToken").value(response.refreshToken()));
        }

        @DisplayName("refresh token은 필수값이며, 빈 문자열이거나 공백일 수 없다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void invalidRefreshToken(String refreshToken) throws Exception {
            // given
            AccessTokenRefreshRequest request = new AccessTokenRefreshRequest(refreshToken);

            // when
            ResultActions resultActions = mockMvc.perform(post("/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.refreshToken")
                    .value("refresh token은 필수값"));
        }
    }
}
