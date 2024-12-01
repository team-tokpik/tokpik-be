package org.example.tokpik_be.talk_topic.service;

import static org.assertj.core.api.Assertions.*;
import static org.example.tokpik_be.talk_topic.dto.response.TalkTopicsRelatedResponse.*;
import static org.example.tokpik_be.util.llm.dto.response.LLMTalkTopicDetailResponse.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.groups.Tuple;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TalkTopicException;
import org.example.tokpik_be.scrap.repository.QueryDslScrapRepository;
import org.example.tokpik_be.talk_topic.domain.TalkPartner;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicDetailResponse;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicsRelatedResponse;
import org.example.tokpik_be.talk_topic.repository.QueryDslTalkTopicRepository;
import org.example.tokpik_be.talk_topic.repository.TalkTopicRepository;
import org.example.tokpik_be.type.domain.PlaceType;
import org.example.tokpik_be.type.domain.TopicType;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.enums.Gender;
import org.example.tokpik_be.user.service.UserQueryService;
import org.example.tokpik_be.util.llm.client.LLMApiClient;
import org.example.tokpik_be.util.llm.dto.request.LLMTalkTopicDetailRequest;
import org.example.tokpik_be.util.llm.dto.response.LLMTalkTopicDetailResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TalkTopicQueryServiceTest {

    private final long TOPIC_ID = 1L;
    private final long USER_ID = 1L;

    @Mock
    private TalkTopicRepository talkTopicRepository;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private QueryDslTalkTopicRepository queryDslTalkTopicRepository;

    @Mock
    private QueryDslScrapRepository queryDslScrapRepository;

    @Mock
    private LLMApiClient llmApiClient;

    @InjectMocks
    private TalkTopicQueryService talkTopicQueryService;

    @Nested
    @DisplayName("Id로 대화 주제 조회 시 ")
    class FindByIdTest {

        @DisplayName("성공한다.")
        @Test
        void success() {
            // given
            TalkTopic talkTopic = Mockito.mock(TalkTopic.class);
            given(talkTopicRepository.findById(TOPIC_ID)).willReturn(Optional.of(talkTopic));

            // when
            TalkTopic result = talkTopicQueryService.findById(TOPIC_ID);

            // then
            assertThat(result).isEqualTo(result);
        }

        @DisplayName("존재하지 않는 대화 주제일 경우 예외가 발생한다.")
        @Test
        void talkTopicNotFound() {
            // given
            given(talkTopicRepository.findById(TOPIC_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> talkTopicQueryService.findById(TOPIC_ID))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(TalkTopicException.TALK_TOPIC_NOT_FOUND);
        }
    }

    @DisplayName("연관 대화 주제 조회 시 성공한다.")
    @Test
    void getRelatedTopics() {
        // given
        User user = mock(User.class);
        given(user.getId()).willReturn(USER_ID);
        given(userQueryService.findById(USER_ID)).willReturn(user);

        TalkTopic talkTopic = mock(TalkTopic.class);
        given(talkTopicRepository.findById(TOPIC_ID)).willReturn(Optional.of(talkTopic));

        List<TalkTopicRelatedResponse> talkTopics = List.of(
            new TalkTopicRelatedResponse(2L, "인간관계", "쓰@껄하게 스몰톡하는 법", false),
            new TalkTopicRelatedResponse(3L, "미팅", "취미 있으세요?", false)
        );

        TalkTopicsRelatedResponse response = new TalkTopicsRelatedResponse(talkTopics);
        given(queryDslTalkTopicRepository.findRelatedTalkTopics(USER_ID, talkTopic)).willReturn(response);

        // when
        TalkTopicsRelatedResponse result = talkTopicQueryService.getRelatedTopics(USER_ID, TOPIC_ID);

        // then
        assertThat(result).isEqualTo(response);
    }

    @DisplayName("대화 주제 상세 내용 조회 시 성공한다.")
    @Test
    void getTalkTopicDetail() {
        // given
        User user = mock(User.class);
        given(userQueryService.findById(USER_ID)).willReturn(user);

        TopicType topicType = new TopicType("비즈니스");
        PlaceType placeType = new PlaceType("직장");
        TalkTopic talkTopic = new TalkTopic("대화 제목",
            "대화 부제목",
            "비즈니스 미팅",
            new TalkPartner(Gender.FEMALE, 20, 30),
            topicType,
            placeType);
        given(talkTopicRepository.findById(TOPIC_ID)).willReturn(Optional.of(talkTopic));

        List<LLMTalkTopicDetailItemResponse> details = IntStream.range(0, 2)
            .mapToObj(i -> new LLMTalkTopicDetailItemResponse("항목 제목", "항목 내용"))
            .toList();
        LLMTalkTopicDetailResponse llmResponse = new LLMTalkTopicDetailResponse(details);
        given(llmApiClient.generateTalkTopicDetail(any(LLMTalkTopicDetailRequest.class))).willReturn(llmResponse);

        given(queryDslScrapRepository.checkTopicScrapedBy(user, talkTopic)).willReturn(false);

        // when
        TalkTopicDetailResponse response = talkTopicQueryService.getTalkTopicDetail(USER_ID, TOPIC_ID);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.scraped()).isEqualTo(false);

            softly.assertThat(response.details()).hasSize(details.size())
                .extracting("itemTitle", "itemContent")
                .containsOnly(Tuple.tuple("항목 제목", "항목 내용"));
        });
    }
}
