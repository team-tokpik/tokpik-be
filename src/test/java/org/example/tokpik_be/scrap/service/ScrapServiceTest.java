package org.example.tokpik_be.scrap.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.ScrapException;
import org.example.tokpik_be.scrap.domain.Scrap;
import org.example.tokpik_be.scrap.domain.ScrapTopic;
import org.example.tokpik_be.scrap.dto.request.ScrapCreateRequest;
import org.example.tokpik_be.scrap.dto.response.ScrapCountResponse;
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
    void getScraps() {
        // given
        long userId = 1L;
        User user = new User("ex@example.com", "https://www.example.com/profile-photo");
        given(userQueryService.findById(userId)).willReturn(user);

        List<TopicType> topicTypes = List.of(
            new TopicType(1L, "요즘 이슈"),
            new TopicType(2L, "비즈니스"));

        List<TalkTopic> talkTopics = List.of(
            new TalkTopic("제목1", "부제목1", "상황1", null, topicTypes.get(0), null),
            new TalkTopic("제목2", "부제목2", "상황2", null, topicTypes.get(1), null));

        List<Scrap> scraps = List.of(
            new Scrap("스크랩 1", user),
            new Scrap("스크랩 2", user));

        List<ScrapTopic> scrapTopics = List.of(
            new ScrapTopic(scraps.get(0),talkTopics.get(0)),
            new ScrapTopic(scraps.get(0),talkTopics.get(1)),
            new ScrapTopic(scraps.get(1),talkTopics.get(0)),
            new ScrapTopic(scraps.get(1),talkTopics.get(1)));

        List<ScrapTopic> scrapTopicsForScrap1 = List.of(scrapTopics.get(0), scrapTopics.get(1));
        List<ScrapTopic> scrapTopicsForScrap2 = List.of(scrapTopics.get(2), scrapTopics.get(3));

        given(scrapRepository.findByUserOrderByCreatedAtDesc(user)).willReturn(scraps);
        given(scrapTopicRepository.findByScrapOrderByCreatedAtDesc(scraps.get(0))).willReturn(scrapTopicsForScrap1);
        given(scrapTopicRepository.findByScrapOrderByCreatedAtDesc(scraps.get(1))).willReturn(scrapTopicsForScrap2);

        // when
        ScrapListResponse response = scrapService.getScraps(userId);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.scraps()).hasSize(scraps.size());

            ScrapListResponse.ScrapResponse firstScrapResponse = response.scraps().get(0);
            softly.assertThat(firstScrapResponse)
                .usingRecursiveComparison()
                .ignoringFields("scrapId")
                .isEqualTo(new ScrapListResponse.ScrapResponse(
                    1L,
                    "스크랩 1",
                    List.of(new ScrapListResponse.TopicTypeResponse(1L, "요즘 이슈"),
                        new ScrapListResponse.TopicTypeResponse(2L, "비즈니스"))
                ));

            ScrapListResponse.TopicTypeResponse firstTopicType = firstScrapResponse.recentTopicTypes().get(0);
            softly.assertThat(firstTopicType)
                .usingRecursiveComparison()
                .isEqualTo(new ScrapListResponse.TopicTypeResponse(1L, "요즘 이슈"));
        });
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

            List<TalkTopic> talkTopics = List.of(
                new TalkTopic("제목1", "부제목1", "상황1", null, new TopicType(1L, "자기 계발"), null),
                new TalkTopic("제목2", "부제목2", "상황2", null, new TopicType(2L, "인간관계"), null));

            List<ScrapTopic> scrapTopics = List.of(
                new ScrapTopic(scrap, talkTopics.get(0)),
                new ScrapTopic(scrap, talkTopics.get(1)));

            given(scrapTopicRepository.findByScrapIdAndIdGreaterThanOrderByIdAsc(scrapId, nextCursorId, PageRequest.of(0, size)))
                .willReturn(scrapTopics);
            given(scrapTopicRepository.existsByScrapIdAndId(scrapId, nextCursorId)).willReturn(true);

            // when
            ScrapResponse response = scrapService.getScrapTopics(scrapId, nextCursorId, size);

            // then
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(response.contents()).hasSize(scrapTopics.size());
                softly.assertThat(response.nextCursorId()).isEqualTo(scrapTopics.get(scrapTopics.size() - 1).getId());
                softly.assertThat(response.first()).isTrue();
            });
        }

        @DisplayName("스크랩이 존재하지 않으면 예외가 발생한다.")
        @Test
        void invalidScrap() {
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
        void invalidTalkTopic() {
            // given
            long scrapId = 1L;
            Scrap scrap = mock(Scrap.class);
            given(scrapRepository.findById(scrapId)).willReturn(Optional.of(scrap));

            long invalidNextCursorId = 999L;
            int size = 3;

            given(scrapRepository.findById(scrapId)).willReturn(Optional.of(scrap));
            given(scrapTopicRepository.existsByScrapIdAndId(scrapId, invalidNextCursorId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> scrapService.getScrapTopics(scrapId, invalidNextCursorId, size))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(ScrapException.INVALID_SCRAP_TOPIC);
        }
    }

    @DisplayName("스크랩의 개수를 조회할 수 있다.")
    @Test
    void getScrapCount() {
        // given
        long userId = 1L;
        User user = new User("ex@example.com", "https://www.example.com/profile-photo");
        given(userQueryService.findById(userId)).willReturn(user);

        given(scrapRepository.countByUser(user)).willReturn(10L);

        // when
        ScrapCountResponse response = scrapService.getUserScrapCount(userId);

        // then
        assertThat(response.count()).isEqualTo(10L);
    }

    @DisplayName("전체 스크랩에 포함된 총 대화주제 개수를 조회할 수 있다.")
    @Test
    void getScrapTopicCount() {
        // given
        long userId = 1L;
        User user = new User("ex@example.com", "https://www.example.com/profile-photo");
        given(userQueryService.findById(userId)).willReturn(user);

        given(scrapTopicRepository.countByUserId(userId)).willReturn(20L);

        // when
        ScrapCountResponse response = scrapService.getUserTopicCount(userId);

        // then
        assertThat(response.count()).isEqualTo(20L);
    }

    @DisplayName("사용자는 스크랩을 삭제할 수 있다.")
    @Test
    void deleteScrap() {
        // given
        long scrapId = 1L;
        Scrap scrap = mock(Scrap.class);
        given(scrapRepository.findById(scrapId)).willReturn(Optional.of(scrap));

        // when
        scrapService.deleteScrap(scrapId);

        // then
        verify(scrapRepository).delete(any(Scrap.class));
    }

    @Nested
    @DisplayName("스크랩된 대화주제 삭제 시 ")
    class DeleteScrapTopicTest {

        @DisplayName("성공한다.")
        @Test
        void deleteScrapTopic() {
            // given
            long scrapId = 1L;
            Scrap scrap = mock(Scrap.class);
            given(scrapRepository.findById(scrapId)).willReturn(Optional.of(scrap));

            long scrapTopicId = 1L;
            ScrapTopic scrapTopic = mock(ScrapTopic.class);
            given(scrapTopicRepository.findById(scrapTopicId)).willReturn(Optional.of(scrapTopic));

            given(scrap.getScrapTopics()).willReturn(List.of(scrapTopic));

            // when
            scrapService.deleteScrapTopic(scrapId, scrapTopicId);

            // then
            verify(scrapTopicRepository).delete(any(ScrapTopic.class));
        }

        @DisplayName("스크랩에 포함되지 않은 대화주제이면 예외가 발생한다.")
        @Test
        void invalidScrapTopic() {
            // given
            long scrapId = 1L;
            Scrap scrap = mock(Scrap.class);
            given(scrapRepository.findById(scrapId)).willReturn(Optional.of(scrap));

            long invalidScrapTopicId = 1L;
            ScrapTopic scrapTopic = mock(ScrapTopic.class);
            given(scrapTopicRepository.findById(invalidScrapTopicId)).willReturn(Optional.of(scrapTopic));

            // when & then
            assertThatThrownBy(() -> scrapService.deleteScrapTopic(scrapId, invalidScrapTopicId))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(ScrapException.INVALID_SCRAP_TOPIC);
        }
    }
}
