package org.example.tokpik_be.login.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.example.tokpik_be.login.dto.request.AccessTokenRefreshRequest;
import org.example.tokpik_be.login.dto.response.AccessTokenRefreshResponse;
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
    @DisplayName("access token 갱신 시 ")
    class RefreshAccessTokenTest {

        @DisplayName("성공한다.")
        @Test
        void success() throws Exception {
            // given
            String jwt = "header.payload.signature";
            AccessTokenRefreshRequest request = new AccessTokenRefreshRequest(jwt);

            AccessTokenRefreshResponse response = new AccessTokenRefreshResponse(jwt);
            given(loginCommandService.refreshAccessToken(request)).willReturn(response);

            // when
            ResultActions resultActions = mockMvc.perform(post("/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(response.accessToken()));
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
