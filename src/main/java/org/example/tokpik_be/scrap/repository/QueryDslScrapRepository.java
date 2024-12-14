package org.example.tokpik_be.scrap.repository;

import static org.example.tokpik_be.scrap.domain.QScrap.*;
import static org.example.tokpik_be.scrap.domain.QScrapTopic.scrapTopic;
import static org.example.tokpik_be.talk_topic.domain.QTalkTopic.*;

import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.example.tokpik_be.scrap.domain.Scrap;
import org.example.tokpik_be.scrap.domain.ScrapTopic;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.user.domain.User;
import org.springframework.data.domain.Pageable;
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

	public Long countScrapBy(User user) {

		Long count = queryFactory.select(scrap.count())
			.from(scrap)
			.where(scrap.user.eq(user))
			.fetchOne();

		return Optional.ofNullable(count).orElse(0L);
	}

	public Long countScrapTopicBy(User user) {

		Long count = queryFactory.select(scrapTopic.count())
			.from(scrapTopic)
			.join(scrapTopic.scrap)
			.where(scrapTopic.scrap.user.eq(user))
			.fetchOne();

		return Optional.ofNullable(count).orElse(0L);
	}

	public boolean checkIsTopicScraped(Long id, Long topicId) {

		Long count = queryFactory.select(scrap.count())
			.from(scrap)
			.innerJoin(scrap.scrapTopics, scrapTopic)
			.innerJoin(scrapTopic.talkTopic, talkTopic)
			.where(scrap.id.eq(id)
				.and(talkTopic.id.eq(topicId)))
			.fetchOne();

		return Optional.ofNullable(count).orElse(0L) > 0;
	}

	public List<Scrap> findScrapBy(User user) {

		return queryFactory.selectFrom(scrap)
			.where(scrap.user.eq(user))
			.orderBy(scrap.createdAt.desc())
			.fetch();
	}

	public List<ScrapTopic> findScrapTopicBy(Scrap scrap) {

		return queryFactory.selectFrom(scrapTopic)
			.where(scrapTopic.scrap.eq(scrap))
			.orderBy(scrapTopic.createdAt.desc())
			.fetch();
	}

	public List<ScrapTopic> findScrapTopicByCursor(Long scrapId, Long nextCursorId, Pageable pageable) {

		return queryFactory.selectFrom(scrapTopic)
			.where(scrapTopic.scrap.id.eq(scrapId)
					.and(scrapTopic.id.gt(nextCursorId)))
			.orderBy(scrapTopic.id.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	public long countScrapTopicByCursor(Long scrapId, Long nextCursorId) {

		return Optional.ofNullable(
			queryFactory.select(scrapTopic.count())
				.from(scrapTopic)
				.where(scrapTopic.scrap.id.eq(scrapId)
					.and(scrapTopic.id.gt(nextCursorId)))
				.fetchOne()
		).orElse(0L);
	}

	public boolean existsBy(Long scrapId, Long topicId) {

		Integer result = queryFactory.selectOne()
			.from(scrapTopic)
			.where(scrapTopic.scrap.id.eq(scrapId)
					.and(scrapTopic.id.eq(topicId)))
			.fetchFirst();

		return Objects.nonNull(result);
	}
}
