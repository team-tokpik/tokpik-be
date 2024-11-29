package org.example.tokpik_be.talk_topic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.talk_topic.dto.request.TalkTopicSearchRequest;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicsSearchResponse;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicsSearchResponse.TalkTopicSearchResponse;
import org.example.tokpik_be.talk_topic.repository.TalkTopicRepository;
import org.example.tokpik_be.type.domain.PlaceType;
import org.example.tokpik_be.type.domain.TopicType;
import org.example.tokpik_be.type.repository.PlaceTypeRepository;
import org.example.tokpik_be.type.repository.TopicTypeRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.example.tokpik_be.util.llm.client.LLMApiClient;
import org.example.tokpik_be.util.llm.dto.request.LLMTalkTopicSearchRequest;
import org.example.tokpik_be.util.llm.dto.response.LLMTalkTopicsResponse;
import org.example.tokpik_be.util.llm.dto.response.LLMTalkTopicsResponse.LLMTalkTopicResponse;
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

    @DisplayName("검색 조건을 기반으로 대화 주제를 생성할 수 있다.")
    @Test
    void generateTopicsBySearchConditions() {
        // given
        long userId = 1L;

        TopicType businessTopicType = mock(TopicType.class);
        given(businessTopicType.getContent()).willReturn("비즈니스");

        TopicType meetingTopicType = mock(TopicType.class);
        given(meetingTopicType.getContent()).willReturn("미팅");

        List<TopicType> topicTypes = List.of(businessTopicType, meetingTopicType);
        given(topicTypeRepository.findAll()).willReturn(topicTypes);

        PlaceType cafePlaceType = mock(PlaceType.class);
        given(cafePlaceType.getContent()).willReturn("카페");

        PlaceType companyPlaceType = mock(PlaceType.class);
        given(companyPlaceType.getContent()).willReturn("회사");

        List<PlaceType> placeTypes = List.of(cafePlaceType, companyPlaceType);
        given(placeTypeRepository.findAll()).willReturn(placeTypes);

        User user = mock(User.class);
        given(userQueryService.findById(userId)).willReturn(user);

        TalkTopicSearchRequest request = new TalkTopicSearchRequest(true,
            List.of("비즈니스", "미팅"),
            List.of("1대1", "첫만남"),
            List.of("편안한 분위기", "자유로운 분위기"),
            false,
            20,
            30);

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
        LLMTalkTopicsResponse llmResponse = new LLMTalkTopicsResponse(llmTalkTopicResponses);
        given(llmApiClient.searchTalkTopics(any(LLMTalkTopicSearchRequest.class)))
            .willReturn(llmResponse);

        TalkTopic businessTalkTopic = mock(TalkTopic.class);
        given(businessTalkTopic.getId()).willReturn(1L);
        given(businessTalkTopic.getTitle()).willReturn(title);
        given(businessTalkTopic.getSubtitle()).willReturn(subtitle);
        given(businessTalkTopic.getTopicType()).willReturn(businessTopicType);
        given(businessTalkTopic.getPlaceType()).willReturn(companyPlaceType);

        TalkTopic meetingTalkTopic = mock(TalkTopic.class);
        given(meetingTalkTopic.getId()).willReturn(2L);
        given(meetingTalkTopic.getTitle()).willReturn(title);
        given(meetingTalkTopic.getSubtitle()).willReturn(subtitle);
        given(meetingTalkTopic.getTopicType()).willReturn(meetingTopicType);
        given(meetingTalkTopic.getPlaceType()).willReturn(cafePlaceType);

        List<TalkTopic> talkTopics = List.of(businessTalkTopic, meetingTalkTopic);
        given(talkTopicRepository.saveAll(any())).willReturn(talkTopics);

        // when
        TalkTopicsSearchResponse response = talkTopicCommandService.generateTopics(userId, request);

        // then
        SoftAssertions.assertSoftly(softly -> {
            assertThat(response.topics()).hasSize(2);

            TalkTopicSearchResponse businessTalkTopicResponse = response.topics().get(0);
            assertThat(businessTalkTopicResponse.topicId()).isEqualTo(businessTalkTopic.getId());
            assertThat(businessTalkTopicResponse.title()).isEqualTo(businessTalkTopic.getTitle());
            assertThat(businessTalkTopicResponse.subtitle())
                .isEqualTo(businessTalkTopic.getSubtitle());
            assertThat(businessTalkTopicResponse.topicTag())
                .isEqualTo(businessTopicType.getContent());
            assertThat(businessTalkTopicResponse.placeTag())
                .isEqualTo(companyPlaceType.getContent());
            assertThat(businessTalkTopicResponse.scraped()).isFalse();

            TalkTopicSearchResponse meetingTalkTopicResponse = response.topics().get(1);
            assertThat(meetingTalkTopicResponse.topicId()).isEqualTo(meetingTalkTopic.getId());
            assertThat(meetingTalkTopicResponse.title()).isEqualTo(meetingTalkTopic.getTitle());
            assertThat(meetingTalkTopicResponse.subtitle())
                .isEqualTo(meetingTalkTopic.getSubtitle());
            assertThat(meetingTalkTopicResponse.topicTag())
                .isEqualTo(meetingTopicType.getContent());
            assertThat(meetingTalkTopicResponse.placeTag()).isEqualTo(cafePlaceType.getContent());
            assertThat(meetingTalkTopicResponse.scraped()).isFalse();
        });
    }
}