package org.example.tokpik_be.tag.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TagException;
import org.example.tokpik_be.exception.UserException;
import org.example.tokpik_be.support.ControllerTestSupport;
import org.example.tokpik_be.tag.dto.request.UserTopicTagsRequest;
import org.example.tokpik_be.tag.dto.response.TopicTagTotalResponse;
import org.example.tokpik_be.tag.dto.response.UserTopicTagResponse;
import org.example.tokpik_be.tag.service.TopicTagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TopicTagControllerTest extends ControllerTestSupport {
    @Mock
    private TopicTagService topicTagService;

    @InjectMocks
    private TopicTagController topicTagController;

    @Override
    protected Object initController() {
        return topicTagController;
    }

    private final long userId = 1L;

    @Nested
    @DisplayName("사용자 대화 태그 조회 시 ")
    class GetUserTopicTagsTest {

        @Test
        @DisplayName("성공한다.")
        void success() throws Exception {
            UserTopicTagResponse response = new UserTopicTagResponse(userId, List.of(
                new UserTopicTagResponse.TopicTagDTO(1L, "Tag 1")
            ));
            given(topicTagService.getUserTopicTags(userId)).willReturn(response);

            ResultActions resultActions = mockMvc.perform(get("/users/tags").requestAttr("userId", userId));

            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.talkTopicTags[0].content").value("Tag 1"));
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외가 발생한다.")
        void userNotFound() throws Exception {
            given(topicTagService.getUserTopicTags(userId))
                .willThrow(new GeneralException(UserException.USER_NOT_FOUND));

            ResultActions resultActions = mockMvc.perform(get("/users/tags").requestAttr("userId", userId));

            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(UserException.USER_NOT_FOUND.getMessage()));
        }
    }

    @Nested
    @DisplayName("사용자 대화 태그 수정 시 ")
    class updateUserTopicTagsTest {

        @Test
        @DisplayName("성공한다.")
        void success() throws Exception {
            // Given
            UserTopicTagsRequest request = new UserTopicTagsRequest(List.of(1L, 2L));
            UserTopicTagResponse response = new UserTopicTagResponse(userId, List.of(
                new UserTopicTagResponse.TopicTagDTO(1L, "Tag 1"),
                new UserTopicTagResponse.TopicTagDTO(2L, "Tag 2")
            ));
            given(topicTagService.updateUserTopicTags(eq(userId), any(UserTopicTagsRequest.class))).willReturn(response);

            // When
            ResultActions resultActions = mockMvc.perform(patch("/users/tags")
                .requestAttr("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)));

            // Then
            resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.talkTopicTags[0].id").value(1L))
                .andExpect(jsonPath("$.talkTopicTags[0].content").value("Tag 1"))
                .andExpect(jsonPath("$.talkTopicTags[1].id").value(2L))
                .andExpect(jsonPath("$.talkTopicTags[1].content").value("Tag 2"));
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외가 발생한다.")
        void userNotFound() throws Exception {
            // Given
            given(topicTagService.getUserTopicTags(userId))
                .willThrow(new GeneralException(UserException.USER_NOT_FOUND));

            // When
            ResultActions resultActions = mockMvc.perform(get("/users/tags")
                .requestAttr("userId", userId));

            // Then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(UserException.USER_NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("요청 데이터가 없으면 예외가 발생한다.")
        void noRequestData() throws Exception {
            //Given
            given(topicTagService.updateUserTopicTags(anyLong(), any(UserTopicTagsRequest.class)))
                .willThrow(new GeneralException(TagException.INVALID_REQUEST));

            // When
            ResultActions resultActions = mockMvc.perform(patch("/users/tags")
                .requestAttr("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));

            // Then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(TagException.INVALID_REQUEST.getMessage()));
        }
    }

    @Nested
    @DisplayName("대화 태그 전체 조회 시 ")
    class getAllTagsTest{
        @Test
        @DisplayName("성공한다.")
        void success() throws Exception{
            // Given
            TopicTagTotalResponse response = new TopicTagTotalResponse(List.of(
                new TopicTagTotalResponse.TopicTagResponse(1L, "Tag 1"),
                new TopicTagTotalResponse.TopicTagResponse(2L, "Tag 2")
            ));
            given(topicTagService.getAllTopicTags()).willReturn(response);

            // When
            ResultActions resultActions = mockMvc.perform(get("/topic-tags"));

            // Then
            resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topicTags[0].content").value("Tag 1"))
                .andExpect(jsonPath("$.topicTags[1].content").value("Tag 2"));
        }
    }
}
