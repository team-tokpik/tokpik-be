package org.example.tokpik_be.scrap.repository;

import org.assertj.core.api.Assertions;
import org.example.tokpik_be.config.QueryDslConfig;
import org.example.tokpik_be.scrap.domain.Scrap;
import org.example.tokpik_be.scrap.domain.ScrapTopic;
import org.example.tokpik_be.talk_topic.domain.TalkPartner;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.type.domain.PlaceType;
import org.example.tokpik_be.type.domain.TopicType;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@ActiveProfiles("test")
@Import(QueryDslConfig.class)
@DataJpaTest
class QueryDslScrapRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    private QueryDslScrapRepository queryDslScrapRepository;

    @BeforeEach
    void setUp() {
        queryDslScrapRepository = new QueryDslScrapRepository(jpaQueryFactory);
    }

    @DisplayName("사용자의 대화 주제 스크랩 여부 조회를 성공한다.")
    @Test
    void checkTopicScrapedBy() {
        // given
        User user = new User("ex@example.com", "https://profile-photo/1");
        em.persist(user);
        em.flush();

        TopicType topicType = new TopicType("비즈니스");
        em.persist(topicType);
        PlaceType placeType = new PlaceType("직장");
        em.persist(placeType);
        em.flush();

        TalkTopic talkTopic = new TalkTopic("대화 제목",
            "대화 부제목",
            "비즈니스 미팅",
            new TalkPartner(Gender.FEMALE, 20, 30),
            topicType,
            placeType);
        em.persist(talkTopic);
        em.flush();

        Scrap scrap = new Scrap("스크랩 제목", user);
        em.persist(scrap);
        em.flush();

        ScrapTopic scrapTopic = new ScrapTopic(scrap, talkTopic);
        em.persist(scrapTopic);
        em.flush();

        // when
        boolean scraped = queryDslScrapRepository.checkTopicScrapedBy(user, talkTopic);

        // then
        Assertions.assertThat(scraped).isTrue();
    }
}
