package org.example.tokpik_be.type.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TypeException;
import org.example.tokpik_be.exception.UserException;
import org.example.tokpik_be.support.ControllerTestSupport;
import org.example.tokpik_be.type.dto.request.UserTopicTypesRequest;
import org.example.tokpik_be.type.dto.response.TopicTypeTotalResponse;
import org.example.tokpik_be.type.dto.response.UserTopicTypeResponse;
import org.example.tokpik_be.type.service.TopicTypeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TopicTypeControllerTest extends ControllerTestSupport {
    @Mock
    private TopicTypeService topicTypeService;

    @InjectMocks
    private TopicTypeController topicTypeController;

    @Override
    protected Object initController() {
        return topicTypeController;
    }

    private final long userId = 1L;

    @Nested
    @DisplayName("사용자 대화 타입 조회 시 ")
    class GetUserTopicTypesTest {

        @Test
        @DisplayName("성공한다.")
        void success() throws Exception {
            // given
            UserTopicTypeResponse response = new UserTopicTypeResponse(userId, List.of(
                new UserTopicTypeResponse.TopicTypeDTO(1L, "사랑과 연애")
            ));
            given(topicTypeService.getUserTopicTypes(userId)).willReturn(response);

            // when
            ResultActions resultActions = mockMvc.perform(get("/users/types").requestAttr("userId", userId));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.talkTopicTypes[0].content").value("사랑과 연애"));
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외가 발생한다.")
        void userNotFound() throws Exception {
            // given
            given(topicTypeService.getUserTopicTypes(userId))
                .willThrow(new GeneralException(UserException.USER_NOT_FOUND));

            // when
            ResultActions resultActions = mockMvc.perform(get("/users/types").requestAttr("userId", userId));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(UserException.USER_NOT_FOUND.getMessage()));
        }
    }

    @Nested
    @DisplayName("사용자 대화 타입 수정 시 ")
    class UpdateUserTopicTypesTest {

        @Test
        @DisplayName("성공한다.")
        void success() throws Exception {
            // given
            UserTopicTypesRequest request = new UserTopicTypesRequest(List.of(1L, 2L));
            UserTopicTypeResponse response = new UserTopicTypeResponse(userId, List.of(
                new UserTopicTypeResponse.TopicTypeDTO(1L, "사랑과 연애"),
                new UserTopicTypeResponse.TopicTypeDTO(2L, "비즈니스와 업무")
            ));
            given(topicTypeService.updateUserTopicTypes(eq(userId), any(UserTopicTypesRequest.class))).willReturn(response);

            // when
            ResultActions resultActions = mockMvc.perform(patch("/users/types")
                .requestAttr("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)));

            // then
            resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.talkTopicTypes[0].id").value(1L))
                .andExpect(jsonPath("$.talkTopicTypes[0].content").value("사랑과 연애"))
                .andExpect(jsonPath("$.talkTopicTypes[1].id").value(2L))
                .andExpect(jsonPath("$.talkTopicTypes[1].content").value("비즈니스와 업무"));
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외가 발생한다.")
        void userNotFound() throws Exception {
            // given
            given(topicTypeService.getUserTopicTypes(userId))
                .willThrow(new GeneralException(UserException.USER_NOT_FOUND));

            // when
            ResultActions resultActions = mockMvc.perform(get("/users/types")
                .requestAttr("userId", userId));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(UserException.USER_NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("요청 데이터가 없으면 예외가 발생한다.")
        void noRequestData() throws Exception {
            // given
            given(topicTypeService.updateUserTopicTypes(anyLong(), any(UserTopicTypesRequest.class)))
                .willThrow(new GeneralException(TypeException.INVALID_REQUEST));

            // when
            ResultActions resultActions = mockMvc.perform(patch("/users/types")
                .requestAttr("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(TypeException.INVALID_REQUEST.getMessage()));
        }
    }

    @Nested
    @DisplayName("대화 타입 전체 조회 시 ")
    class GetAllTypesTest{
        @Test
        @DisplayName("성공한다.")
        void success() throws Exception{
            // given
            TopicTypeTotalResponse response = new TopicTypeTotalResponse(List.of(
                new TopicTypeTotalResponse.TopicTypeResponse(1L, "사랑과 연애"),
                new TopicTypeTotalResponse.TopicTypeResponse(2L, "비즈니스와 업무")
            ));
            given(topicTypeService.getAllTopicTypes()).willReturn(response);

            // when
            ResultActions resultActions = mockMvc.perform(get("/topic-types"));

            // then
            resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topicTypes[0].content").value("사랑과 연애"))
                .andExpect(jsonPath("$.topicTypes[1].content").value("비즈니스와 업무"));
        }
    }
}
