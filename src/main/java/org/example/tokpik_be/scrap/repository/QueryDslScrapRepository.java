package org.example.tokpik_be.scrap.repository;

import static org.example.tokpik_be.scrap.domain.QScrap.*;
import static org.example.tokpik_be.scrap.domain.QScrapTopic.scrapTopic;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
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

	public Long countByUser(User user) {
		Long count = queryFactory
			.select(scrap.count())
			.from(scrap)
			.where(scrap.user.eq(user))
			.fetchOne();

		return Optional.ofNullable(count).orElse(0L);
	}
}
