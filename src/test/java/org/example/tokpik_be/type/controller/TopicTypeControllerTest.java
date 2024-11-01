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
    @DisplayName("사용자 대화 태그 조회 시 ")
    class GetUserTopicTypesTest {

        @Test
        @DisplayName("성공한다.")
        void success() throws Exception {
            UserTopicTypeResponse response = new UserTopicTypeResponse(userId, List.of(
                new UserTopicTypeResponse.TopicTypeDTO(1L, "Type 1")
            ));
            given(topicTypeService.getUserTopicTypes(userId)).willReturn(response);

            ResultActions resultActions = mockMvc.perform(get("/users/types").requestAttr("userId", userId));

            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.talkTopicTypes[0].content").value("Type 1"));
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외가 발생한다.")
        void userNotFound() throws Exception {
            given(topicTypeService.getUserTopicTypes(userId))
                .willThrow(new GeneralException(UserException.USER_NOT_FOUND));

            ResultActions resultActions = mockMvc.perform(get("/users/types").requestAttr("userId", userId));

            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(UserException.USER_NOT_FOUND.getMessage()));
        }
    }

    @Nested
    @DisplayName("사용자 대화 태그 수정 시 ")
    class updateUserTopicTypesTest {

        @Test
        @DisplayName("성공한다.")
        void success() throws Exception {
            // Given
            UserTopicTypesRequest request = new UserTopicTypesRequest(List.of(1L, 2L));
            UserTopicTypeResponse response = new UserTopicTypeResponse(userId, List.of(
                new UserTopicTypeResponse.TopicTypeDTO(1L, "Type 1"),
                new UserTopicTypeResponse.TopicTypeDTO(2L, "Type 2")
            ));
            given(topicTypeService.updateUserTopicTypes(eq(userId), any(UserTopicTypesRequest.class))).willReturn(response);

            // When
            ResultActions resultActions = mockMvc.perform(patch("/users/types")
                .requestAttr("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)));

            // Then
            resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.talkTopicTypes[0].id").value(1L))
                .andExpect(jsonPath("$.talkTopicTypes[0].content").value("Type 1"))
                .andExpect(jsonPath("$.talkTopicTypes[1].id").value(2L))
                .andExpect(jsonPath("$.talkTopicTypes[1].content").value("Type 2"));
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외가 발생한다.")
        void userNotFound() throws Exception {
            // Given
            given(topicTypeService.getUserTopicTypes(userId))
                .willThrow(new GeneralException(UserException.USER_NOT_FOUND));

            // When
            ResultActions resultActions = mockMvc.perform(get("/users/types")
                .requestAttr("userId", userId));

            // Then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(UserException.USER_NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("요청 데이터가 없으면 예외가 발생한다.")
        void noRequestData() throws Exception {
            //Given
            given(topicTypeService.updateUserTopicTypes(anyLong(), any(UserTopicTypesRequest.class)))
                .willThrow(new GeneralException(TypeException.INVALID_REQUEST));

            // When
            ResultActions resultActions = mockMvc.perform(patch("/users/types")
                .requestAttr("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));

            // Then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(TypeException.INVALID_REQUEST.getMessage()));
        }
    }

    @Nested
    @DisplayName("대화 태그 전체 조회 시 ")
    class getAllTypesTest{
        @Test
        @DisplayName("성공한다.")
        void success() throws Exception{
            // Given
            TopicTypeTotalResponse response = new TopicTypeTotalResponse(List.of(
                new TopicTypeTotalResponse.TopicTypeResponse(1L, "Type 1"),
                new TopicTypeTotalResponse.TopicTypeResponse(2L, "Type 2")
            ));
            given(topicTypeService.getAllTopicTypes()).willReturn(response);

            // When
            ResultActions resultActions = mockMvc.perform(get("/topic-types"));

            // Then
            resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topicTypes[0].content").value("Type 1"))
                .andExpect(jsonPath("$.topicTypes[1].content").value("Type 2"));
        }
    }
}
