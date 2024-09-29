package org.example.tokpik_be.talk_topic.repository;

import static org.example.tokpik_be.scrap.domain.QScrap.scrap;
import static org.example.tokpik_be.scrap.domain.QScrapTopic.scrapTopic;
import static org.example.tokpik_be.tag.domain.QPlaceTag.placeTag;
import static org.example.tokpik_be.tag.domain.QTopicTag.topicTag;
import static org.example.tokpik_be.talk_topic.domain.QTalkTopic.talkTopic;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicsRelatedResponse;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicsRelatedResponse.TalkTopicRelatedResponse;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslTalkTopicRepository {

    private final JPAQueryFactory queryFactory;

    public TalkTopicsRelatedResponse findRelatedTalkTopics(long userId, TalkTopic baseTopic) {
        List<TalkTopicRelatedResponse> relatedTopics = queryFactory.from(talkTopic)
            .select(Projections.constructor(TalkTopicRelatedResponse.class,
                talkTopic.id,
                talkTopic.topicTag.content,
                talkTopic.title,
                JPAExpressions.selectOne()
                    .from(scrapTopic)
                    .innerJoin(scrapTopic.scrap, scrap)
                    .where(scrapTopic.talkTopic.id.eq(talkTopic.id).and(scrap.user.id.eq(userId)))
                    .exists()
                    .as("scraped")
            ))
            .innerJoin(talkTopic.topicTag, topicTag)
            .innerJoin(talkTopic.placeTag, placeTag)
            .where(
                talkTopic.topicTag.id.eq(baseTopic.getTopicTag().getId()),
                talkTopic.placeTag.id.eq(baseTopic.getPlaceTag().getId()),
                talkTopic.situation.contains(baseTopic.getSituation()),
                talkTopic.partner.gender.eq(baseTopic.getPartner().getGender()),
                talkTopic.partner.ageLowerBound.goe(baseTopic.getPartner().getAgeLowerBound()),
                talkTopic.partner.ageUpperBound.loe(baseTopic.getPartner().getAgeUpperBound()),
                talkTopic.id.ne(baseTopic.getId()))
            .limit(10)
            .fetch();

        return new TalkTopicsRelatedResponse(relatedTopics);
    }
}
