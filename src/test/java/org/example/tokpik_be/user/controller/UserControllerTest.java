package org.example.tokpik_be.user.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.example.tokpik_be.support.ControllerTestSupport;
import org.example.tokpik_be.user.dto.request.UserMakeProfileRequest;
import org.example.tokpik_be.user.dto.request.UserUpdateNotificationTokenRequest;
import org.example.tokpik_be.user.dto.response.UserProfileResponse;
import org.example.tokpik_be.user.service.UserCommandService;
import org.example.tokpik_be.user.service.UserQueryService;
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

class UserControllerTest extends ControllerTestSupport {

    private final long userId = 1L;

    @Mock
    private UserCommandService userCommandService;

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
    }

    @Nested
    @DisplayName("프로필 생성 시 ")
    class MakeProfileTest {

        private final UserMakeProfileRequest request = new UserMakeProfileRequest(
            LocalDate.now().minusYears(20),
            true,
            List.of(1L, 2L, 3L),
            List.of(1L, 2L, 3L));

        @DisplayName("성공한다.")
        @Test
        void success() throws Exception {
            // given
            doNothing().when(userCommandService).makeProfile(userId, request);

            // when
            ResultActions resultActions = mockMvc.perform(post("/users/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isOk());
        }

        @DisplayName("생년월일은 필수값이다.")
        @Test
        void withoutBirth() throws Exception {
            // given
            UserMakeProfileRequest requestWithoutBirth = new UserMakeProfileRequest(null,
                request.gender(),
                request.topicTagIds(),
                request.placeTagIds());

            // when
            ResultActions resultActions = mockMvc.perform(post("/users/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(objectMapper.writeValueAsString(requestWithoutBirth)));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.birth").value("생년월일은 필수값"));
        }

        @DisplayName("생년월일은 과거,현재만 가능하다.")
        @Test
        void futureBirth() throws Exception {
            // given
            UserMakeProfileRequest futureBirthRequest = new UserMakeProfileRequest(
                LocalDate.now().plusYears(1),
                request.gender(),
                request.topicTagIds(),
                request.placeTagIds());

            // when
            ResultActions resultActions = mockMvc.perform(post("/users/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(objectMapper.writeValueAsString(futureBirthRequest)));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.birth")
                    .value("생년월일은 과거,현재만 가능"));
        }

        @DisplayName("대화 장소 ID 목록에 null이 포함될 수 없다.")
        @Test
        void includeNullPlaceTagIds() throws Exception {
            // given
            UserMakeProfileRequest invalidTopicTagIdsRequest = new UserMakeProfileRequest(
                request.birth(),
                request.gender(),
                Arrays.asList(1L, null, 3L),
                request.topicTagIds());

            // when
            ResultActions resultActions = mockMvc.perform(post("/users/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(objectMapper.writeValueAsString(invalidTopicTagIdsRequest)));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message['placeTagIds[1]']")
                    .value("placeTagId는 not null"));
        }

        @DisplayName("대화 종류 ID 목록에 null이 포함될 수 없다.")
        @Test
        void includeNullTopicTagIds() throws Exception {
            // given
            UserMakeProfileRequest invalidPlaceTagIdsRequest = new UserMakeProfileRequest(
                request.birth(),
                request.gender(),
                request.topicTagIds(),
                Arrays.asList(1L, null, 3L));

            // when
            ResultActions resultActions = mockMvc.perform(post("/users/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(objectMapper.writeValueAsString(invalidPlaceTagIdsRequest)));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message['topicTagIds[1]']")
                    .value("topicTagId는 not null"));
        }
    }

    @Nested
    @DisplayName("notification token 갱신 시 ")
    class UpdateNotificationTokenTest {

        @DisplayName("성공한다.")
        @Test
        void success() throws Exception {
            // given
            String notificationToken = "header.payload.signature";
            UserUpdateNotificationTokenRequest request = new UserUpdateNotificationTokenRequest(
                notificationToken);

            willDoNothing().given(userCommandService).updateNotificationToken(userId, request);

            // when
            ResultActions resultActions = mockMvc.perform(put("/users/notification-token")
                .requestAttr("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isOk());
        }

        @DisplayName("notification token은 필수값이며 유효한 문자열이어야 한다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void invalidNotificationToken(String notificationToken) throws Exception {
            // given
            UserUpdateNotificationTokenRequest request = new UserUpdateNotificationTokenRequest(
                notificationToken);

            // when
            ResultActions resultActions = mockMvc.perform(put("/users/notification-token")
                .requestAttr("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.notificationToken")
                    .value("notification token은 필수값"));
        }
    }

    @Nested
    @DisplayName("회원 탈퇴 시 ")
    class DeleteUserTest {

        @DisplayName("성공한다.")
        @Test
        void success() throws Exception {
            // given
            willDoNothing().given(userCommandService).deleteUser(userId);

            // when
            ResultActions resultActions = mockMvc.perform(delete("/users")
                .requestAttr("userId", userId));

            // then
            resultActions.andExpect(status().isOk());
        }
    }
}
