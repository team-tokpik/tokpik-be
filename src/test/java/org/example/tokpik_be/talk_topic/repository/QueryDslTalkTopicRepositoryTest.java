package org.example.tokpik_be.talk_topic.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.IntStream;
import org.assertj.core.api.SoftAssertions;
import org.example.tokpik_be.config.QueryDslConfig;
import org.example.tokpik_be.scrap.domain.Scrap;
import org.example.tokpik_be.scrap.domain.ScrapTopic;
import org.example.tokpik_be.tag.domain.PlaceTag;
import org.example.tokpik_be.tag.domain.TopicTag;
import org.example.tokpik_be.talk_topic.domain.TalkPartner;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicsRelatedResponse;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicsRelatedResponse.TalkTopicRelatedResponse;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.enums.Gender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Import(QueryDslConfig.class)
@DataJpaTest
class QueryDslTalkTopicRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    private QueryDslTalkTopicRepository queryDslTalkTopicRepository;

    private User user;
    private TopicTag topicTag;
    private PlaceTag placeTag;
    private TalkTopic baseTopic;
    private TalkPartner talkPartner;

    @BeforeEach
    void setUp() {
        queryDslTalkTopicRepository = new QueryDslTalkTopicRepository(jpaQueryFactory);

        // 사용자 저장
        user = new User("mj3242@naver.com", "profile_photo_url");
        em.persist(user);
        em.flush();

        // 대화 종류 태그 저장
        topicTag = new TopicTag("example-topicTag");
        em.persist(topicTag);
        em.flush();

        // 대화 장소 태그 저장
        placeTag = new PlaceTag("example-placeTag");
        em.persist(placeTag);
        em.flush();

        // 6개의 대화 주제들 저장
        talkPartner = new TalkPartner(Gender.MALE, 20, 29);
        List<TalkTopic> talkTopics = IntStream.range(0, 7)
            .mapToObj(i -> new TalkTopic("title",
                "subtitle",
                "situation",
                talkPartner,
                topicTag,
                placeTag))
            .toList();
        baseTopic = talkTopics.get(0);

        talkTopics.forEach(em::persist);
        em.flush();

        // 스크랩 생성
        Scrap scrap = new Scrap("example-scrap", user);
        em.persist(scrap);
        em.flush();

        // 4개의 스크랩 대화 주제 저장
        List<TalkTopic> scrapTalkTopics = talkTopics.subList(0, 4);
        scrapTalkTopics.stream()
            .map(talkTopic -> new ScrapTopic(scrap, talkTopic))
            .forEach(em::persist);
        em.flush();
    }

    @AfterEach
    void tearDown() {
        em.clear();
        em.createQuery("DELETE FROM ScrapTopic").executeUpdate();
        em.createQuery("DELETE FROM Scrap").executeUpdate();
        em.createQuery("DELETE FROM TalkTopic").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.createQuery("DELETE FROM TopicTag").executeUpdate();
        em.createQuery("DELETE FROM PlaceTag").executeUpdate();
        em.flush();
    }

    @DisplayName("연관 주제를 조회할 수 있다.")
    @Test
    void findRelatedTalkTopics() {
        // given

        // when
        TalkTopicsRelatedResponse result = queryDslTalkTopicRepository
            .findRelatedTalkTopics(user.getId(), baseTopic);

        // then
        List<TalkTopicRelatedResponse> relatedTalkTopics = result.talkTopics();
        SoftAssertions.assertSoftly(softly -> {
            // 연관된 주제 모두 조회하였는 지 검증
            softly.assertThat(relatedTalkTopics).hasSize(6);

            // 조회된 주제들의 대화 종류가 모두 일치하는 지 검증
            List<String> topicTagContents = relatedTalkTopics.stream()
                .map(TalkTopicRelatedResponse::type)
                .toList();
            softly.assertThat(topicTagContents).allMatch(
                topicTagContent -> baseTopic.getTopicTag().getContent().equals(topicTagContent));

            // 조회된 주제들중 스크랩된 주제들의 스크랩 여부 검증
            List<TalkTopicRelatedResponse> scrapedTalkTopics = relatedTalkTopics.stream()
                .filter(TalkTopicRelatedResponse::scraped)
                .toList();
            softly.assertThat(scrapedTalkTopics).hasSize(3);
        });
    }

    @DisplayName("연관 대화 주제 조회시 다른 대화 장소인 주제는 조회하지 않는다.")
    @Test
    void differentPlaceTag() {
        // given
        PlaceTag otherPlaceTag = new PlaceTag("집");
        em.persist(otherPlaceTag);
        em.flush();

        List<TalkTopic> unrelatedTalkTopics = IntStream.range(0, 2)
            .mapToObj(i -> new TalkTopic("title",
                "subtitle",
                "situation",
                talkPartner,
                topicTag,
                otherPlaceTag))
            .toList();
        unrelatedTalkTopics.forEach(em::persist);
        em.flush();

        // when
        TalkTopicsRelatedResponse result = queryDslTalkTopicRepository
            .findRelatedTalkTopics(user.getId(), baseTopic);

        // then
        List<Long> unrelatedTalkTopicIds = extractTalkTopicIds(unrelatedTalkTopics);
        List<Long> relatedTalkTopicIds = extractTopicIdsFromResult(result);
        assertThat(relatedTalkTopicIds).doesNotContainAnyElementsOf(unrelatedTalkTopicIds);
    }

    @DisplayName("연관 대화 주제 조회시 다른 대화 종류인 주제는 조회하지 않는다.")
    @Test
    void differentTopicTag() {
        // given
        TopicTag otherTopicTag = new TopicTag("비즈니스");
        em.persist(otherTopicTag);
        em.flush();

        List<TalkTopic> unrelatedTalkTopics = IntStream.range(0, 2)
            .mapToObj(i -> new TalkTopic("title",
                "subtitle",
                "situation",
                talkPartner,
                otherTopicTag,
                placeTag))
            .toList();
        unrelatedTalkTopics.forEach(em::persist);
        em.flush();

        // when
        TalkTopicsRelatedResponse result = queryDslTalkTopicRepository
            .findRelatedTalkTopics(user.getId(), baseTopic);

        // then
        List<Long> unrelatedTalkTopicIds = extractTalkTopicIds(unrelatedTalkTopics);
        List<Long> relatedTalkTopicIds = extractTopicIdsFromResult(result);
        assertThat(relatedTalkTopicIds).doesNotContainAnyElementsOf(unrelatedTalkTopicIds);
    }

    @DisplayName("연관 대화 주제 조회시 기준 주제 대화 상황을 포함하지 않는 주제는 조회하지 않는다.")
    @Test
    void doesNotIncludeBaseTopicSituation() {
        // given
        String otherSituation = "otherSituation";
        List<TalkTopic> unrelatedTalkTopics = IntStream.range(0, 2)
            .mapToObj(i -> new TalkTopic("title",
                "subtitle",
                otherSituation,
                talkPartner,
                topicTag,
                placeTag))
            .toList();
        unrelatedTalkTopics.forEach(em::persist);
        em.flush();

        // when
        TalkTopicsRelatedResponse result = queryDslTalkTopicRepository
            .findRelatedTalkTopics(user.getId(), baseTopic);

        // then
        List<Long> unrelatedTalkTopicIds = extractTalkTopicIds(unrelatedTalkTopics);
        List<Long> relatedTalkTopicIds = extractTopicIdsFromResult(result);
        assertThat(relatedTalkTopicIds).doesNotContainAnyElementsOf(unrelatedTalkTopicIds);
    }

    @DisplayName("연관 대화 주제 조회시 기준 주제의 대화 상대 성별과 일치하는 주제만을 조회한다.")
    @Test
    void differentPartnerGender() {
        // given
        Gender differentGender = Gender.FEMALE;
        TalkPartner differentPartner = new TalkPartner(differentGender,
            talkPartner.getAgeLowerBound(),
            talkPartner.getAgeUpperBound());

        List<TalkTopic> unrelatedTalkTopics = IntStream.range(0, 2)
            .mapToObj(i -> new TalkTopic("title",
                "subtitle",
                "situation",
                differentPartner,
                topicTag,
                placeTag))
            .toList();
        unrelatedTalkTopics.forEach(em::persist);
        em.flush();

        // when
        TalkTopicsRelatedResponse result = queryDslTalkTopicRepository
            .findRelatedTalkTopics(user.getId(), baseTopic);

        // then
        List<Long> unrelatedTalkTopicIds = extractTalkTopicIds(unrelatedTalkTopics);
        List<Long> relatedTalkTopicIds = extractTopicIdsFromResult(result);
        assertThat(relatedTalkTopicIds).doesNotContainAnyElementsOf(unrelatedTalkTopicIds);
    }

    @DisplayName("연관 대화 주제 조회시 기준 주제 대화 상대 연령대에 포함되는 주제만을 조회한다.")
    @Test
    void outOfPartnerAgeRange() {
        // given
        List<TalkPartner> talkPartners = List.of(
            new TalkPartner(talkPartner.getGender(),
                talkPartner.getAgeLowerBound() - 1,
                talkPartner.getAgeUpperBound()),
            new TalkPartner(talkPartner.getGender(),
                talkPartner.getAgeLowerBound(),
                talkPartner.getAgeUpperBound() + 1
            ));
        List<TalkTopic> unrelatedTalkTopics = talkPartners.stream().map(partner -> new TalkTopic(
                "title",
                "subtitle",
                "situation",
                partner,
                topicTag,
                placeTag))
            .toList();
        unrelatedTalkTopics.forEach(em::persist);
        em.flush();

        // when
        TalkTopicsRelatedResponse result = queryDslTalkTopicRepository
            .findRelatedTalkTopics(user.getId(), baseTopic);

        // then
        List<Long> unrelatedTalkTopicIds = extractTalkTopicIds(unrelatedTalkTopics);
        List<Long> relatedTalkTopicIds = extractTopicIdsFromResult(result);
        assertThat(relatedTalkTopicIds).doesNotContainAnyElementsOf(unrelatedTalkTopicIds);
    }

    @DisplayName("연관 주제 조회시 기준 주제는 포함되지 않는다.")
    @Test
    void notIncludeBaseTopic() {
        // given

        // when
        TalkTopicsRelatedResponse result = queryDslTalkTopicRepository
            .findRelatedTalkTopics(user.getId(), baseTopic);

        // then
        List<Long> relatedTalkTopicIds = extractTopicIdsFromResult(result);
        assertThat(relatedTalkTopicIds).doesNotContain(baseTopic.getId());
    }

    private List<Long> extractTalkTopicIds(List<TalkTopic> talkTopics) {

        return talkTopics.stream().map(TalkTopic::getId).toList();
    }

    private List<Long> extractTopicIdsFromResult(TalkTopicsRelatedResponse result) {

        return result.talkTopics().stream().map(TalkTopicRelatedResponse::topicId).toList();
    }
}