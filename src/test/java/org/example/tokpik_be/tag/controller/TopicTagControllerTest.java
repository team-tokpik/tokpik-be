package org.example.tokpik_be.tag.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.UserException;
import org.example.tokpik_be.support.ControllerTestSupport;
import org.example.tokpik_be.tag.controller.TopicTagController;
import org.example.tokpik_be.tag.dto.response.UserTopicTagResponse;
import org.example.tokpik_be.tag.service.TopicTagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.ResultActions;

public class TopicTagControllerTest extends ControllerTestSupport {
    @Mock
    private TopicTagService topicTagService;

    @InjectMocks
    private TopicTagController topicTagController;

    @Override
    protected Object initController() {

        return topicTagController;
    }

    @Nested
    @DisplayName("사용자 대화 태그 조회 시")
    class GetUserTopicTagsTest {

        private final long userId = 1L;

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
}
