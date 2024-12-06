package org.example.tokpik_be.scrap.repository;

import static org.example.tokpik_be.scrap.domain.QScrap.*;
import static org.example.tokpik_be.scrap.domain.QScrapTopic.scrapTopic;

import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.example.tokpik_be.scrap.domain.QScrap;
import org.example.tokpik_be.scrap.domain.QScrapTopic;
import org.example.tokpik_be.scrap.domain.Scrap;
import org.example.tokpik_be.scrap.domain.ScrapTopic;
import org.example.tokpik_be.talk_topic.domain.QTalkTopic;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.user.domain.User;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslScrapRepository {

    private final JPAQueryFactory queryFactory;

    public boolean checkTopicScrapedBy(User user, TalkTopic talkTopic) {

        Integer result = queryFactory.selectOne()
            .from(scrapTopic)
            .where(scrapTopic.scrap.user.id.eq(user.getId())
                .and(scrapTopic.talkTopic.id.eq(talkTopic.getId())))
            .fetchOne();

        return Objects.nonNull(result);
    }

	public Long countScrapByUser(User user) {
		Long count = queryFactory
			.select(scrap.count())
			.from(scrap)
			.where(scrap.user.eq(user))
			.fetchOne();

		return Optional.ofNullable(count).orElse(0L);
	}

	public Long countScrapTopicByUser(User user) {
		QScrapTopic scrapTopic = QScrapTopic.scrapTopic;

		Long count =  queryFactory
			.select(scrapTopic.count())
			.from(scrapTopic)
			.join(scrapTopic.scrap)
			.where(scrapTopic.scrap.user.eq(user))
			.fetchOne();

		return Optional.ofNullable(count).orElse(0L);
	}

	public boolean checkIsTopicScraped(Long id, Long topicId) {
		QScrap qScrap = QScrap.scrap;
		QScrapTopic qScrapTopic = QScrapTopic.scrapTopic;
		QTalkTopic qTalkTopic = QTalkTopic.talkTopic;

		Long count = queryFactory
			.select(qScrap.count())
			.from(qScrap)
			.innerJoin(qScrap.scrapTopics, qScrapTopic)
			.innerJoin(qScrapTopic.talkTopic, qTalkTopic)
			.where(qScrap.id.eq(id)
				.and(qTalkTopic.id.eq(topicId)))
			.fetchOne();

		return Optional.ofNullable(count).orElse(0L) > 0;
	}

	public List<Scrap> findByUser(User user){
		QScrap qScrap = QScrap.scrap;

		return queryFactory
			.selectFrom(qScrap)
			.where(qScrap.user.eq(user))
			.orderBy(qScrap.createdAt.desc())
			.fetch();
	}

	public List<ScrapTopic> findScrapTopicBy(Scrap scrap) {
		QScrapTopic qScrapTopic = QScrapTopic.scrapTopic;

		return queryFactory
			.selectFrom(qScrapTopic)
			.where(qScrapTopic.scrap.eq(scrap))
			.orderBy(qScrapTopic.createdAt.desc())
			.fetch();
	}
}
