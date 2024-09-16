package org.example.tokpik_be.user.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.UserException;
import org.example.tokpik_be.support.ControllerTestSupport;
import org.example.tokpik_be.user.dto.response.UserProfileResponse;
import org.example.tokpik_be.user.service.UserQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.ResultActions;

class UserControllerTest extends ControllerTestSupport {

    @Mock
    private UserQueryService userQueryService;

    @InjectMocks
    private UserController userController;

    @Override
    protected Object initController() {

        return userController;
    }

    @Nested
    @DisplayName("마이 페이지 프로필 조회 시 ")
    class GetMyProfileTest {

        private final long userId = 1L;

        @DisplayName("성공한다.")
        @Test
        void success() throws Exception {
            // given
            UserProfileResponse response = new UserProfileResponse("mj****@naver.com",
                "profile-photo/1-0");
            given(userQueryService.getUserProfile(userId)).willReturn(response);

            // when
            ResultActions resultActions = mockMvc.perform(get("/users/profiles")
                .requestAttr("userId", userId));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.maskedEmail").value(response.maskedEmail()))
                .andExpect(jsonPath("$.profilePhotoUrl").value(response.profilePhotoUrl()));
        }

        @DisplayName("사용자가 존재하지 않으면 예외가 발생한다.")
        @Test
        void userNotFound() throws Exception {
            // given
            given(userQueryService.getUserProfile(userId))
                .willThrow(new GeneralException(UserException.USER_NOT_FOUND));

            // when
            ResultActions resultActions = mockMvc.perform(get("/users/profiles")
                .requestAttr("userId", userId));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(UserException.USER_NOT_FOUND.getMessage()));
        }
    }
}