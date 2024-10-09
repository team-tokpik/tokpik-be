package org.example.tokpik_be.scrap.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.UserException;
import org.example.tokpik_be.scrap.dto.response.ScrapListResponse;
import org.example.tokpik_be.scrap.service.ScrapService;
import org.example.tokpik_be.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.ResultActions;

public class ScrapControllerTest extends ControllerTestSupport {

    @Mock
    private ScrapService scrapService;

    @InjectMocks
    private ScrapController scrapController;

    @Override
    protected Object initController(){
        return scrapController;
    }

    private final long userId = 1L;

    @Nested
    @DisplayName("스크랩 리스트 목록 조회 시 ")
    class getScrapListTest{

        @Test
        @DisplayName("성공한다.")
        void success() throws Exception {

            // Given
            ScrapListResponse response = new ScrapListResponse(List.of(
                new ScrapListResponse.ScrapResponse(1L, "스크랩 1", List.of(
                    new ScrapListResponse.TopicTypeResponse(3L, "아이스브레이킹"),
                    new ScrapListResponse.TopicTypeResponse(4L, "유머와 웃음")
                )),
                new ScrapListResponse.ScrapResponse(2L, "스크랩 2", List.of(
                    new ScrapListResponse.TopicTypeResponse(1L,"사랑과 연애"),
                    new ScrapListResponse.TopicTypeResponse(7L,"자기 계발"),
                    new ScrapListResponse.TopicTypeResponse(8L,"인간관계")
                ))
            ));
            given(scrapService.getScrapList(userId)).willReturn(response);

            // When
            ResultActions resultActions = mockMvc.perform(get("/users/scraps")
                .requestAttr("userId",userId));

            // Then
            resultActions
                .andExpect(status().isOk())
                // .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.scraps.[0].scrapId").value(1L))
                .andExpect(jsonPath("$.scraps[0].scrapName").value("스크랩 1"))
                .andExpect(jsonPath("$.scraps[0].recentTopicTypes[0].topicTypeId").value(3L))
                .andExpect(jsonPath("$.scraps[0].recentTopicTypes[0].topicTypeContent").value("아이스브레이킹"))
                .andExpect(jsonPath("$.scraps[0].recentTopicTypes[1].topicTypeId").value(4L))
                .andExpect(jsonPath("$.scraps[0].recentTopicTypes[1].topicTypeContent").value("유머와 웃음"))
                .andExpect(jsonPath("$.scraps[1].scrapId").value(2L))
                .andExpect(jsonPath("$.scraps[1].scrapName").value("스크랩 2"))
                .andExpect(jsonPath("$.scraps[1].recentTopicTypes[0].topicTypeId").value(1L))
                .andExpect(jsonPath("$.scraps[1].recentTopicTypes[0].topicTypeContent").value("사랑과 연애"))
                .andExpect(jsonPath("$.scraps[1].recentTopicTypes[1].topicTypeId").value(7L))
                .andExpect(jsonPath("$.scraps[1].recentTopicTypes[1].topicTypeContent").value("자기 계발"))
                .andExpect(jsonPath("$.scraps[1].recentTopicTypes[2].topicTypeId").value(8L))
                .andExpect(jsonPath("$.scraps[1].recentTopicTypes[2].topicTypeContent").value("인간관계"));
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외가 발생한다.")
        void userNotFound() throws Exception {
            // Given
            given(scrapService.getScrapList(userId))
                .willThrow(new GeneralException(UserException.USER_NOT_FOUND));

            // When
            ResultActions resultActions = mockMvc.perform(get("/users/scraps").requestAttr("userId", userId));

            // Then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(UserException.USER_NOT_FOUND.getMessage()));
        }
    }

}

