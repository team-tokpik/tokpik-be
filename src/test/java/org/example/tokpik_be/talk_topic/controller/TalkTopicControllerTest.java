package org.example.tokpik_be.talk_topic.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.example.tokpik_be.support.ControllerTestSupport;
import org.example.tokpik_be.talk_topic.dto.request.TalkTopicSearchRequest;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicsSearchResponse;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicsSearchResponse.TalkTopicSearchResponse;
import org.example.tokpik_be.talk_topic.service.TalkTopicCommandService;
import org.example.tokpik_be.talk_topic.service.TalkTopicQueryService;
import org.example.tokpik_be.user.enums.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;


class TalkTopicControllerTest extends ControllerTestSupport {

    @Mock
    private TalkTopicCommandService talkTopicCommandService;

    @Mock
    private TalkTopicQueryService talkTopicQueryService;

    @InjectMocks
    private TalkTopicController talkTopicController;

    @Override
    protected Object initController() {

        return talkTopicController;
    }

    @Nested
    @DisplayName("대화 주제 조회 시 ")
    class SearchTalkTopicsTest {

        private final List<String> talkPurposes = List.of("친목", "비즈니스");
        private final List<String> talkSituations = List.of("첫만남", "1대1");
        private final List<String> talkMoods = List.of("편안한분위기", "조용한분위기");
        private final boolean talkPartnerGender = Gender.MALE.toBoolean();
        private final int talkPartnerAgeLowerBound = 20;
        private final int talkPartnerAgeUpperBound = 30;

        @DisplayName("성공한다.")
        @Test
        void success() throws Exception {
            // given
            TalkTopicSearchRequest request = new TalkTopicSearchRequest(true,
                talkPurposes,
                talkSituations,
                talkMoods,
                talkPartnerGender,
                talkPartnerAgeLowerBound,
                talkPartnerAgeUpperBound);

            List<TalkTopicSearchResponse> talkTopicSearchResponses = List.of(
                new TalkTopicSearchResponse(1L,
                    "대화 주제 제목 1",
                    "대화 주제 부제목 1",
                    "친목",
                    "장소태그1",
                    true));
            TalkTopicsSearchResponse response = new TalkTopicsSearchResponse(
                talkTopicSearchResponses);

            given(talkTopicCommandService.generateTopics(any(Long.class), eq(request)))
                .willReturn(response);

            // when
            ResultActions resultActions = mockMvc.perform(post("/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.topics.length()")
                    .value(talkTopicSearchResponses.size()))
                .andExpect(jsonPath("$.topics[0].topicId")
                    .value(1L))
                .andExpect(jsonPath("$.topics[0].title")
                    .value("대화 주제 제목 1"))
                .andExpect(jsonPath("$.topics[0].subtitle")
                    .value("대화 주제 부제목 1"))
                .andExpect(jsonPath("$.topics[0].topicTag")
                    .value("친목"))
                .andExpect(jsonPath("$.topics[0].placeTag")
                    .value("장소태그1"))
                .andExpect(jsonPath("$.topics[0].scraped")
                    .value(true));
        }

        private static Stream<Arguments> provideInvalidTalkPurposes() {

            return java.util.stream.Stream.of(Arguments.of(Arrays.asList(null, "친목")),
                Arguments.of(List.of("", "친목")),
                Arguments.of(List.of(" ", "친목"))
            );
        }

        @DisplayName("대화목적은 유효한 문자열만 가능하다.")
        @ParameterizedTest
        @MethodSource("provideInvalidTalkPurposes")
        void invalidTalkPurpose(List<String> talkPurposes) throws Exception {
            // given
            TalkTopicSearchRequest request = new TalkTopicSearchRequest(true,
                talkPurposes,
                talkSituations,
                talkMoods,
                talkPartnerGender,
                talkPartnerAgeLowerBound,
                talkPartnerAgeUpperBound);

            // when
            ResultActions resultActions = mockMvc.perform(post("/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message['talkPurposes[0]']")
                    .value("대화목적은 유효한 문자열만 가능"));
        }

        private static Stream<Arguments> provideInvalidTalkSituations() {

            return Stream.of(Arguments.of(Arrays.asList(null, "첫만남")),
                Arguments.of(List.of("", "첫만남")),
                Arguments.of(List.of(" ", "첫만남")));
        }

        @DisplayName("대화상황은 유효한 문자열만 가능하다.")
        @ParameterizedTest
        @MethodSource("provideInvalidTalkSituations")
        void invalidTalkSituation(List<String> talkSituations) throws Exception {
            // given
            TalkTopicSearchRequest request = new TalkTopicSearchRequest(true,
                talkPurposes,
                talkSituations,
                talkMoods,
                talkPartnerGender,
                talkPartnerAgeLowerBound,
                talkPartnerAgeUpperBound);

            // when
            ResultActions resultActions = mockMvc.perform(post("/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message['talkSituations[0]']")
                    .value("대화상황은 유효한 문자열만 가능"));
        }

        private static Stream<Arguments> provideInvalidTalkMoods() {

            return Stream.of(Arguments.of(Arrays.asList(null, "편안한분위기")),
                Arguments.of(List.of("", "편안한분위기")),
                Arguments.of(List.of(" ", "편안한분위기")));
        }

        @DisplayName("대화분위기는 유효한 문자열만 가능하다.")
        @ParameterizedTest
        @MethodSource("provideInvalidTalkMoods")
        void invalidTalkMood(List<String> talkMoods) throws Exception {
            // given
            TalkTopicSearchRequest request = new TalkTopicSearchRequest(true,
                talkPurposes,
                talkSituations,
                talkMoods,
                talkPartnerGender,
                talkPartnerAgeLowerBound,
                talkPartnerAgeUpperBound);

            // when
            ResultActions resultActions = mockMvc.perform(post("/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message['talkMoods[0]']")
                    .value("대화분위기는 유효한 문자열만 가능"));
        }
    }
}
