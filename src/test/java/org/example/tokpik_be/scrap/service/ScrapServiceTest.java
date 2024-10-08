package org.example.tokpik_be.scrap.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.ScrapException;
import org.example.tokpik_be.scrap.domain.Scrap;
import org.example.tokpik_be.scrap.domain.ScrapTopic;
import org.example.tokpik_be.scrap.dto.response.ScrapListResponse;
import org.example.tokpik_be.scrap.dto.response.ScrapResponse;
import org.example.tokpik_be.support.ServiceTestSupport;
import org.example.tokpik_be.tag.domain.PlaceTag;
import org.example.tokpik_be.tag.domain.TopicTag;
import org.example.tokpik_be.talk_topic.domain.TalkPartner;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.talk_topic.service.TalkTopicQueryService;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.enums.Gender;
import org.example.tokpik_be.user.service.UserQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

public class ScrapServiceTest extends ServiceTestSupport {

    private ScrapService scrapService;

    @MockBean
    private UserQueryService userQueryService;

    @MockBean
    private TalkTopicQueryService talkTopicQueryService;

    @BeforeEach
    void setUp() {
        scrapService = new ScrapService(scrapRepository, scrapTopicRepository, userQueryService, talkTopicQueryService);
    }

    @Nested
    @DisplayName("스크랩 리스트 조회 시 ")
    class getUserScrapListTest {

        @Test
        @DisplayName("성공한다.")
        void getMyScrapList(){
            User user = new User("test@test.com", "profile-photo/1");
            userRepository.save(user);
            em.flush();
            Long userId = user.getId();

            Scrap scrap1 = new Scrap("스크랩1", user);
            Scrap scrap2 = new Scrap("스크랩2", user);
            scrapRepository.saveAll(Arrays.asList(scrap1, scrap2));

            TopicTag topicTag1 = new TopicTag("유머와 웃음");
            TopicTag topicTag2 = new TopicTag("요즘 핫한 이슈");
            topicTagRepository.saveAll(Arrays.asList(topicTag1, topicTag2));

            TalkTopic talkTopic1 = new TalkTopic("제목1", "부제목1", "상황1", null, topicTag1, null);
            TalkTopic talkTopic2 = new TalkTopic("제목2", "부제목2", "상황2", null, topicTag2, null);
            em.persist(talkTopic1);
            em.persist(talkTopic2);

            ScrapTopic scrapTopic1 = new ScrapTopic(scrap1, talkTopic1);
            ScrapTopic scrapTopic2 = new ScrapTopic(scrap1, talkTopic2);
            scrapTopicRepository.saveAll(Arrays.asList(scrapTopic1, scrapTopic2));

            em.flush();
            em.clear();

            when(userQueryService.findById(userId)).thenReturn(user);

            // When
            ScrapListResponse response = scrapService.getScrapList(userId);

            // Then
            assertThat(response.scraps()).hasSize(2);
            assertThat(response.scraps().get(0).scrapId()).isEqualTo(scrap2.getId());
            assertThat(response.scraps().get(0).scrapName()).isEqualTo("스크랩2");
            assertThat(response.scraps().get(0).recentTopicTypes()).hasSize(0);
        }
    }

    @Nested
    @DisplayName("스크랩에 포함된 대화 주제 조회 시 ")
    class getScrapTopicsTest{

        @Test
        @DisplayName("성공한다.")
        void getScrapTopics(){

            // Given
            User user = new User("test@test.com", "profile-photo/1");
            userRepository.save(user);

            Scrap scrap = new Scrap("스크랩 1", user);
            scrapRepository.save(scrap);

            TopicTag topicTag = new TopicTag("아이스브레이킹");
            topicTagRepository.save(topicTag);

            PlaceTag placeTag = new PlaceTag("카페");
            placeTagRepository.save(placeTag);

            TalkPartner talkpartner = new TalkPartner(Gender.MALE, 10, 99);
            TalkTopic talkTopic = new TalkTopic("영화 이야기로 시작하기", "가장 최근에 본 영화는?", "1대1만남",
                talkpartner, topicTag, placeTag);
            em.persist(talkTopic);

            ScrapTopic scrapTopic = new ScrapTopic(scrap, talkTopic);
            scrapTopicRepository.save(scrapTopic);

            em.flush();
            em.clear();

            // When
            ScrapResponse response = scrapService.getScrapTopics(scrap.getId(), 0L, 10);

            // Then
            assertThat(response.contents()).hasSize(1);
            assertThat(response.contents().get(0).topicId()).isEqualTo(talkTopic.getId());
            assertThat(response.contents().get(0).topicTitle()).isEqualTo("영화 이야기로 시작하기");
        }

        @Test
        @DisplayName("해당 스크랩에 존재하지 않는 대화주제이면 예외가 발생한다.")
        void invalidScrapTopic(){

            // Given
            User user = new User("test@test.com", "profile-photo/1");
            userRepository.save(user);

            Scrap scrap = new Scrap("스크랩 1", user);
            scrapRepository.save(scrap);
            em.flush();

            Long nonExistScrapTopicId = 99L;

            // Then
            assertThatThrownBy(() -> scrapService.getScrapTopics(1L, nonExistScrapTopicId, 10))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ScrapException.INVALID_SCRAP_TOPIC.getMessage());
        }
    }
}
