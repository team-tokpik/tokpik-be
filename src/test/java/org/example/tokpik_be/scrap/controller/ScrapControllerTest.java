package org.example.tokpik_be.scrap.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.ScrapException;
import org.example.tokpik_be.exception.UserException;
import org.example.tokpik_be.scrap.dto.request.ScrapCreateRequest;
import org.example.tokpik_be.scrap.dto.response.ScrapCreateResponse;
import org.example.tokpik_be.scrap.dto.response.ScrapListResponse;
import org.example.tokpik_be.scrap.dto.response.ScrapResponse;
import org.example.tokpik_be.scrap.service.ScrapService;
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

public class ScrapControllerTest extends ControllerTestSupport {

    private final long userId = 1L;
    private final long nextCursorId = 0L;
    private final int size = 10;
    private final long scrapId = 10L;

    @Mock
    private ScrapService scrapService;

    @InjectMocks
    private ScrapController scrapController;

    @Override
    protected Object initController() {
        return scrapController;
    }

    @DisplayName("대화 주제 스크랩 시 성공한다.")
    @Test
    void scrapTopicSuccess() throws Exception {
        // given
        long scrapId = 1L;
        long topicId = 1L;

        willDoNothing().given(scrapService).scrapTopic(eq(scrapId), eq(topicId));

        // when
        ResultActions resultActions = mockMvc.perform(
            post("/users/scraps/{scrapId}/topics/{topicId}", scrapId, topicId));

        // then
        resultActions.andExpect(status().isOk());
    }

    @Nested
    @DisplayName("스크랩 리스트 목록 조회 시 ")
    class GetScrapListTest {

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
                    new ScrapListResponse.TopicTypeResponse(1L, "사랑과 연애"),
                    new ScrapListResponse.TopicTypeResponse(7L, "자기 계발"),
                    new ScrapListResponse.TopicTypeResponse(8L, "인간관계")
                ))
            ));
            given(scrapService.getScraps(userId)).willReturn(response);

            // When
            ResultActions resultActions = mockMvc.perform(get("/users/scraps"));

            // Then
            resultActions
                .andExpect(status().isOk())
                // .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.scraps.[0].scrapId").value(1L))
                .andExpect(jsonPath("$.scraps[0].scrapName").value("스크랩 1"))
                .andExpect(jsonPath("$.scraps[0].recentTopicTypes[0].topicTypeId").value(3L))
                .andExpect(
                    jsonPath("$.scraps[0].recentTopicTypes[0].topicTypeContent").value("아이스브레이킹"))
                .andExpect(jsonPath("$.scraps[0].recentTopicTypes[1].topicTypeId").value(4L))
                .andExpect(
                    jsonPath("$.scraps[0].recentTopicTypes[1].topicTypeContent").value("유머와 웃음"))
                .andExpect(jsonPath("$.scraps[1].scrapId").value(2L))
                .andExpect(jsonPath("$.scraps[1].scrapName").value("스크랩 2"))
                .andExpect(jsonPath("$.scraps[1].recentTopicTypes[0].topicTypeId").value(1L))
                .andExpect(
                    jsonPath("$.scraps[1].recentTopicTypes[0].topicTypeContent").value("사랑과 연애"))
                .andExpect(jsonPath("$.scraps[1].recentTopicTypes[1].topicTypeId").value(7L))
                .andExpect(
                    jsonPath("$.scraps[1].recentTopicTypes[1].topicTypeContent").value("자기 계발"))
                .andExpect(jsonPath("$.scraps[1].recentTopicTypes[2].topicTypeId").value(8L))
                .andExpect(
                    jsonPath("$.scraps[1].recentTopicTypes[2].topicTypeContent").value("인간관계"));
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외가 발생한다.")
        void userNotFound() throws Exception {
            // Given
            given(scrapService.getScraps(userId))
                .willThrow(new GeneralException(UserException.USER_NOT_FOUND));

            // When
            ResultActions resultActions = mockMvc.perform(get("/users/scraps"));

            // Then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(UserException.USER_NOT_FOUND.getMessage()));
        }
    }

    @Nested
    @DisplayName("스크랩에 포함된 대화 주제 조회 시 ")
    class GetScrapTopicsTest {

        @Test
        @DisplayName("성공한다.")
        void success() throws Exception {

            // Given
            ScrapResponse response = new ScrapResponse(
                List.of(
                    new ScrapResponse.ScrapTopicResponse(1L, 1L, "첫만남에 스몰토크", "아이스브레이킹", true),
                    new ScrapResponse.ScrapTopicResponse(2L, 4L, "재밌는 이야기", "유머와 웃음", false)
                ),
                2L, true, false
            );
            given(scrapService.getScrapTopics(scrapId, nextCursorId, size)).willReturn(response);

            // When
            ResultActions resultActions = mockMvc
                .perform(get("/users/scraps/{scrapId}/topics", scrapId)
                    .param("nextCursorId", String.valueOf(nextCursorId))
                    .param("size", String.valueOf(size)));

            // Then
            resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents.[0].scrapTopicId").value(1L))
                .andExpect(jsonPath("$.contents.[0].topicId").value(1L))
                .andExpect(jsonPath("$.contents.[0].topicTitle").value("첫만남에 스몰토크"))
                .andExpect(jsonPath("$.contents.[0].topicType").value("아이스브레이킹"))
                .andExpect(jsonPath("$.contents.[0].scraped").value(true))
                .andExpect(jsonPath("$.contents.[1].scrapTopicId").value(2L))
                .andExpect(jsonPath("$.contents.[1].topicId").value(4L))
                .andExpect(jsonPath("$.contents.[1].topicTitle").value("재밌는 이야기"))
                .andExpect(jsonPath("$.contents.[1].topicType").value("유머와 웃음"))
                .andExpect(jsonPath("$.contents.[1].scraped").value(false))
                .andExpect(jsonPath("$.nextCursorId").value(2L))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));
        }

        @Test
        @DisplayName("스크랩이 존재하지 않으면 예외가 발생한다.")
        void scrapNotFound() throws Exception {
            // Given
            given(scrapService.getScrapTopics(scrapId, nextCursorId, size))
                .willThrow(new GeneralException(ScrapException.SCRAP_NOT_FOUND));

            // When
            ResultActions resultActions = mockMvc
                .perform(get("/users/scraps/{scrapId}/topics", scrapId)
                    .param("nextCursorId", String.valueOf(nextCursorId))
                    .param("size", String.valueOf(size)));

            // Then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(
                    jsonPath("$.message").value(ScrapException.SCRAP_NOT_FOUND.getMessage()));
        }
    }

    @Nested
    @DisplayName("스크랩 생성 시 ")
    class CreateScrapTest {

        @DisplayName("성공한다.")
        @Test
        void success() throws Exception {
            // given
            ScrapCreateRequest request = new ScrapCreateRequest("스크랩 이름");

            ScrapCreateResponse response = new ScrapCreateResponse(1L);
            given(scrapService.createScrap(any(Long.class), eq(request))).willReturn(response);

            // when
            ResultActions resultActions = mockMvc.perform(post("/users/scraps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.scrapId").value(response.scrapId()));
        }

        @DisplayName("스크랩 이름은 필수값이며 유효한 문자열이어야 한다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void invalidScrapName(String scrapName) throws Exception {
            // given
            ScrapCreateRequest request = new ScrapCreateRequest(scrapName);

            // when
            ResultActions resultActions = mockMvc.perform(post("/users/scraps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message['scrapName']")
                    .value("스크랩 이름은 필수값"));
        }
    }
}

