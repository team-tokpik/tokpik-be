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
import org.example.tokpik_be.type.domain.PlaceType;
import org.example.tokpik_be.type.domain.TopicType;
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

            TopicType topicType = new TopicType("아이스브레이킹");
            topicTypeRepository.save(topicType);

            PlaceType placeType = new PlaceType("카페");
            placeTypeRepository.save(placeType);

            TalkPartner talkpartner = new TalkPartner(Gender.MALE, 10, 99);
            TalkTopic talkTopic = new TalkTopic("영화 이야기로 시작하기", "가장 최근에 본 영화는?", "1대1만남",
                talkpartner, topicType, placeType);
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
