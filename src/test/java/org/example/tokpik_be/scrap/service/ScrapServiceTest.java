package org.example.tokpik_be.scrap.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.ScrapException;
import org.example.tokpik_be.scrap.domain.Scrap;
import org.example.tokpik_be.scrap.domain.ScrapTopic;
import org.example.tokpik_be.scrap.dto.request.ScrapCreateRequest;
import org.example.tokpik_be.scrap.dto.response.ScrapCreateResponse;
import org.example.tokpik_be.scrap.dto.response.ScrapListResponse;
import org.example.tokpik_be.scrap.dto.response.ScrapResponse;
import org.example.tokpik_be.scrap.repository.ScrapRepository;
import org.example.tokpik_be.scrap.repository.ScrapTopicRepository;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.talk_topic.service.TalkTopicQueryService;
import org.example.tokpik_be.type.domain.TopicType;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
public class ScrapServiceTest {

    @Mock
    private ScrapRepository scrapRepository;

    @Mock
    private ScrapTopicRepository scrapTopicRepository;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private TalkTopicQueryService talkTopicQueryService;

    @InjectMocks
    private ScrapService scrapService;

    @DisplayName("사용자는 스크랩을 생성할 수 있다.")
    @Test
    void createScrap() {
        // given
        long userId = 1L;
        User user = new User("ex@example.com", "https://www.example.com/profile-photo");
        given(userQueryService.findById(userId)).willReturn(user);

        long scrapId = 1L;
        Scrap savedScrap = mock(Scrap.class);
        given(savedScrap.getId()).willReturn(scrapId);
        given(scrapRepository.save(any(Scrap.class))).willReturn(savedScrap);

        ScrapCreateRequest request = new ScrapCreateRequest("스크랩 이름");

        // when
        ScrapCreateResponse response = scrapService.createScrap(userId, request);

        // then
        assertThat(response.scrapId()).isEqualTo(scrapId);
    }

    @DisplayName("사용자는 대화 주제를 스크랩할 수 있다.")
    @Test
    void scrapTopic() {
        // given
        long scrapId = 1L;
        Scrap scrap = mock(Scrap.class);
        given(scrapRepository.findById(scrapId)).willReturn(Optional.of(scrap));

        long topicId = 1L;
        TalkTopic talkTopic = mock(TalkTopic.class);
        given(talkTopicQueryService.findById(topicId)).willReturn(talkTopic);

        // when
        scrapService.scrapTopic(scrapId, topicId);

        // then
        verify(scrapTopicRepository).save(any(ScrapTopic.class));
    }
    
    @DisplayName("스크랩 목록을 조회할 수 있다.")
    @Test
    void getScraps(){
        // given
        long userId = 1L;
        User user = new User("ex@example.com", "https://www.example.com/profile-photo");
        given(userQueryService.findById(userId)).willReturn(user);

        TopicType topicType1 = new TopicType(1L, "요즘 이슈");
        TopicType topicType2 = new TopicType(2L, "비즈니스");
        TalkTopic talkTopic1 = new TalkTopic("제목1", "부제목1", "상황1", null, topicType1, null);
        TalkTopic talkTopic2 = new TalkTopic("제목2", "부제목2", "상황2", null, topicType2, null);

        Scrap scrap1 = new Scrap("스크랩 1", user);
        Scrap scrap2 = new Scrap("스크랩 2", user);

        ScrapTopic scrapTopic1 = new ScrapTopic(scrap1, talkTopic1);
        ScrapTopic scrapTopic2 = new ScrapTopic(scrap1, talkTopic2);
        ScrapTopic scrapTopic3 = new ScrapTopic(scrap2, talkTopic1);
        ScrapTopic scrapTopic4 = new ScrapTopic(scrap2, talkTopic2);

        List<Scrap> scraps = List.of(scrap1, scrap2);
        List<ScrapTopic> scrapTopicsForScrap1 = List.of(scrapTopic1, scrapTopic2);
        List<ScrapTopic> scrapTopicsForScrap2 = List.of(scrapTopic3, scrapTopic4);

        given(scrapRepository.findByUserOrderByCreatedAtDesc(user)).willReturn(scraps);
        given(scrapTopicRepository.findByScrapOrderByCreatedAtDesc(scrap1)).willReturn(scrapTopicsForScrap1);
        given(scrapTopicRepository.findByScrapOrderByCreatedAtDesc(scrap2)).willReturn(scrapTopicsForScrap2);

        // when
        ScrapListResponse response = scrapService.getScraps(userId);

        // then
        assertThat(response.scraps()).hasSize(2);

        ScrapListResponse.ScrapResponse firstScrapResponse = response.scraps().get(0);
        assertThat(firstScrapResponse.scrapName()).isEqualTo("스크랩 1");
        assertThat(firstScrapResponse.recentTopicTypes()).hasSize(2);

        ScrapListResponse.TopicTypeResponse firstTopicType = firstScrapResponse.recentTopicTypes().get(0);
        assertThat(firstTopicType.topicTypeId()).isEqualTo(1L);
        assertThat(firstTopicType.topicTypeContent()).isEqualTo("요즘 이슈");
    }

    @Nested
    @DisplayName("스크랩 조회 시 ")
    class GetScrapTest {

        @DisplayName("성공한다.")
        @Test
        void getScrap() {
            // given
            long scrapId = 1L;
            Scrap scrap = mock(Scrap.class);
            given(scrapRepository.findById(scrapId)).willReturn(Optional.of(scrap));

            long nextCursorId = 1L;
            int size = 3;

            TalkTopic talkTopic1 = new TalkTopic("제목1", "부제목1", "상황1", null, new TopicType(1L, "자기 계발"), null);
            TalkTopic talkTopic2 = new TalkTopic("제목2", "부제목2", "상황2", null, new TopicType(2L, "인간관계"), null);

            ScrapTopic scrapTopic1 = new ScrapTopic(scrap, talkTopic1);
            ScrapTopic scrapTopic2 = new ScrapTopic(scrap, talkTopic2);

            List<ScrapTopic> scrapTopics = List.of(scrapTopic1, scrapTopic2);

            when(scrapTopicRepository.findByScrapIdAndIdGreaterThanOrderByIdAsc(scrapId, nextCursorId, PageRequest.of(0, size)))
                .thenReturn(scrapTopics);

            when(scrapTopicRepository.existsByScrapIdAndId(scrapId, nextCursorId)).thenReturn(true);

            // when
            ScrapResponse response = scrapService.getScrapTopics(scrapId, nextCursorId, size);

            // then
            assertThat(response.contents()).hasSize(scrapTopics.size());
            assertThat(response.nextCursorId()).isEqualTo(scrapTopics.get(scrapTopics.size() - 1).getId());
            assertThat(response.first()).isTrue();
        }

        @DisplayName("스크랩이 존재하지 않으면 예외가 발생한다.")
        @Test
        void invalidScrap(){
            // given
            long invalidScrapId = 1L;
            given(scrapRepository.findById(invalidScrapId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> scrapService.getScrapTopics(invalidScrapId, 0L, 3))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(ScrapException.SCRAP_NOT_FOUND);
        }

        @DisplayName("스크랩에 포함되지 않은 대화주제를 페이징에 사용하면 예외가 발생한다.")
        @Test
        void invalidTalkTopic(){
            // given
            long scrapId = 1L;
            Scrap scrap = mock(Scrap.class);
            given(scrapRepository.findById(scrapId)).willReturn(Optional.of(scrap));

            long invalidNextCursorId = 999L;
            int size = 3;

            given(scrapRepository.findById(scrapId)).willReturn(Optional.of(scrap));

            when(scrapTopicRepository.existsByScrapIdAndId(scrapId, invalidNextCursorId)).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> scrapService.getScrapTopics(scrapId, invalidNextCursorId, size))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(ScrapException.INVALID_SCRAP_TOPIC);
        }
    }
}
