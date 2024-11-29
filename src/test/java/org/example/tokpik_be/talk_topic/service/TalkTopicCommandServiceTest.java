package org.example.tokpik_be.talk_topic.service;

import static org.assertj.core.api.Assertions.*;
import static org.example.tokpik_be.talk_topic.dto.response.TalkTopicsSearchResponse.*;
import static org.example.tokpik_be.util.llm.dto.response.LLMTalkTopicsResponse.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.talk_topic.dto.request.TalkTopicSearchRequest;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicsSearchResponse;
import org.example.tokpik_be.talk_topic.repository.TalkTopicRepository;
import org.example.tokpik_be.type.domain.PlaceType;
import org.example.tokpik_be.type.domain.TopicType;
import org.example.tokpik_be.type.domain.UserPlaceType;
import org.example.tokpik_be.type.domain.UserTopicType;
import org.example.tokpik_be.type.repository.PlaceTypeRepository;
import org.example.tokpik_be.type.repository.TopicTypeRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.example.tokpik_be.util.llm.client.LLMApiClient;
import org.example.tokpik_be.util.llm.dto.request.LLMTalkTopicSearchRequest;
import org.example.tokpik_be.util.llm.dto.response.LLMTalkTopicsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TalkTopicCommandServiceTest {

    @Mock
    private TopicTypeRepository topicTypeRepository;

    @Mock
    private PlaceTypeRepository placeTypeRepository;

    @Mock
    private TalkTopicRepository talkTopicRepository;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private LLMApiClient llmApiClient;

    @InjectMocks
    private TalkTopicCommandService talkTopicCommandService;

    private final TopicType businessTopicType = new TopicType("비즈니스");

    private final TopicType meetingTopicType = new TopicType("미팅");

    private final PlaceType cafePlaceType = new PlaceType("카페");

    private final PlaceType companyPlaceType = new PlaceType("회사");

    private User setupMockUser(long userId) {
        User user = mock(User.class);
        given(userQueryService.findById(userId)).willReturn(user);

        return user;
    }

    private LLMTalkTopicsResponse setupMockLLMResponse() {
        String title = "대화 제목";
        String subtitle = "대화 부제목";

        List<LLMTalkTopicResponse> llmTalkTopicResponses = List.of(
            new LLMTalkTopicResponse(title,
                subtitle,
                "비즈니스",
                "회사",
                "1대1",
                false,
                20,
                30),
            new LLMTalkTopicResponse(title,
                subtitle,
                "미팅",
                "카페",
                "첫만남",
                false,
                20,
                30)
        );

        return new LLMTalkTopicsResponse(llmTalkTopicResponses);
    }

    private TalkTopic createMockTalkTopic(long id,
        TopicType topicType,
        PlaceType placeType) {
        TalkTopic talkTopic = mock(TalkTopic.class);
        given(talkTopic.getId()).willReturn(id);
        given(talkTopic.getTitle()).willReturn("대화 제목");
        given(talkTopic.getSubtitle()).willReturn("대화 부제목");
        given(talkTopic.getTopicType()).willReturn(topicType);
        given(talkTopic.getPlaceType()).willReturn(placeType);

        return talkTopic;
    }

    @DisplayName("검색 조건을 기반으로 대화 주제를 생성할 수 있다.")
    @Test
    void generateTopicsBySearchConditions() {
        // given
        long userId = 1L;

        given(topicTypeRepository.findAll()).willReturn(List.of(businessTopicType, meetingTopicType));
        given(placeTypeRepository.findAll()).willReturn(List.of(cafePlaceType, companyPlaceType));
        given(llmApiClient.searchTalkTopics(any(LLMTalkTopicSearchRequest.class))).willReturn(setupMockLLMResponse());

        TalkTopicSearchRequest request = new TalkTopicSearchRequest(true,
            List.of("비즈니스", "미팅"),
            List.of("1대1", "첫만남"),
            List.of("편안한 분위기", "자유로운 분위기"),
            false,
            20,
            30);

        TalkTopic businessTalkTopic = createMockTalkTopic(1L, businessTopicType, companyPlaceType);
        TalkTopic meetingTalkTopic = createMockTalkTopic(2L, meetingTopicType, cafePlaceType);
        given(talkTopicRepository.saveAll(anyList())).willReturn(List.of(businessTalkTopic, meetingTalkTopic));

        // when
        TalkTopicsSearchResponse response = talkTopicCommandService.generateTopics(userId, request);

        // then
        assertGeneratedTopics(response, List.of(businessTalkTopic, meetingTalkTopic));
    }

    @DisplayName("사용자 조건 기반으로 대화 주제를 생성할 수 있다.")
    @Test
    void generateTalkTopicsByUserConditions() {
        // given
        long userId = 1L;
        User user = setupMockUser(userId);

        given(topicTypeRepository.findAll()).willReturn(List.of(businessTopicType, meetingTopicType));
        given(placeTypeRepository.findAll()).willReturn(List.of(cafePlaceType, companyPlaceType));

        UserTopicType userBusinessTopicType = mock(UserTopicType.class);
        given(userBusinessTopicType.getTopicType()).willReturn(businessTopicType);
        UserTopicType userMeetingTopicType = mock(UserTopicType.class);
        given(userMeetingTopicType.getTopicType()).willReturn(meetingTopicType);

        UserPlaceType userCafePlaceType = mock(UserPlaceType.class);
        given(userCafePlaceType.getPlaceType()).willReturn(cafePlaceType);
        UserPlaceType userCompanyPlaceType = mock(UserPlaceType.class);
        given(userCompanyPlaceType.getPlaceType()).willReturn(companyPlaceType);

        given(user.getUserTopicTypes()).willReturn(List.of(userBusinessTopicType, userMeetingTopicType));
        given(user.getUserPlaceTypes()).willReturn(List.of(userCafePlaceType, userCompanyPlaceType));
        given(llmApiClient.searchTalkTopics(any(LLMTalkTopicSearchRequest.class))).willReturn(setupMockLLMResponse());

        TalkTopicSearchRequest request = new TalkTopicSearchRequest(false,
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            null,
            null,
            null);

        TalkTopic businessTalkTopic = createMockTalkTopic(1L, businessTopicType, companyPlaceType);
        TalkTopic meetingTalkTopic = createMockTalkTopic(2L, meetingTopicType, cafePlaceType);
        given(talkTopicRepository.saveAll(any())).willReturn(List.of(businessTalkTopic, meetingTalkTopic));

        // when
        TalkTopicsSearchResponse response = talkTopicCommandService.generateTopics(userId, request);

        // then
        assertGeneratedTopics(response, List.of(businessTalkTopic, meetingTalkTopic));
    }

    private void assertGeneratedTopics(TalkTopicsSearchResponse response, List<TalkTopic> expectedTopics) {
        SoftAssertions.assertSoftly(softly -> softly.assertThat(response.topics()).hasSize(expectedTopics.size())
            .extracting(TalkTopicSearchResponse::topicId,
                TalkTopicSearchResponse::title,
                TalkTopicSearchResponse::subtitle,
                TalkTopicSearchResponse::topicTag,
                TalkTopicSearchResponse::placeTag,
                TalkTopicSearchResponse::scraped)
            .containsExactlyInAnyOrderElementsOf(expectedTopics.stream()
                .map(expected -> tuple(expected.getId(),
                    expected.getTitle(),
                    expected.getSubtitle(),
                    expected.getTopicType().getContent(),
                    expected.getPlaceType().getContent(),
                    false))
                .toList()));
    }
}
