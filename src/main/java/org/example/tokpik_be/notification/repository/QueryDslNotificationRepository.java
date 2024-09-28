package org.example.tokpik_be.notification.repository;

import static org.example.tokpik_be.notification.domain.QNotification.notification;
import static org.example.tokpik_be.notification.domain.QNotificationTalkTopic.notificationTalkTopic;
import static org.example.tokpik_be.scrap.domain.QScrap.scrap;
import static org.example.tokpik_be.talk_topic.domain.QTalkTopic.talkTopic;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.notification.dto.response.NotificationsResponse;
import org.example.tokpik_be.notification.dto.response.NotificationsResponse.NotificationResponse;
import org.example.tokpik_be.notification.dto.response.NotificationsResponse.NotificationResponse.NotificationTalkTopicTypeResponse;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslNotificationRepository {

    private final JPAQueryFactory queryFactory;

    public NotificationsResponse getNotifications(long userId,
        long nextNotificationId,
        int pageSize) {
        // 서브쿼리 : 각 알림에 대해 최대 3개의 대화 주제 선택
        JPQLQuery<Tuple> topicSubQuery = JPAExpressions.select(notificationTalkTopic.notificationId,
                talkTopic.id,
                talkTopic.title)
            .from(notificationTalkTopic)
            .join(talkTopic).on(notificationTalkTopic.talkTopic.id.eq(talkTopic.id))
            .where(notificationTalkTopic.notificationId.eq(notification.id))
            .orderBy(notificationTalkTopic.id.asc())
            .limit(3);

        List<Tuple> results = queryFactory.from(notification)
            .select(notification.id,
                notification.noticeDate,
                notification.startTime,
                notification.endTime,
                notification.intervalMinutes,
                scrap.title,
                JPAExpressions.select(notificationTalkTopic.count())
                    .from(notificationTalkTopic)
                    .where(notificationTalkTopic.notificationId.eq(notification.id)),
                topicSubQuery.select(talkTopic.id),
                topicSubQuery.select(talkTopic.title))
            .join(scrap).on(notification.scrap.id.eq(scrap.id))
            .where(notification.user.id.eq(userId)
                .and(notification.deleted.isFalse())
                .and(notification.id.lt(nextNotificationId)))
            .orderBy(notification.noticeDate.asc(), notification.startTime.asc())
            .limit(pageSize + 1)
            .fetch();

        List<NotificationResponse> contents = results.stream()
            .collect(Collectors.groupingBy(tuple -> tuple.get(notification.id),
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    tuples -> {
                        Tuple firstTuple = tuples.get(0);
                        List<NotificationTalkTopicTypeResponse> topicTypes = tuples.stream()
                            .map(t -> new NotificationTalkTopicTypeResponse(
                                t.get(7, Long.class),
                                t.get(8, String.class)
                            ))
                            .toList();

                        return new NotificationResponse(firstTuple.get(notification.id),
                            firstTuple.get(notification.noticeDate),
                            firstTuple.get(notification.startTime),
                            firstTuple.get(notification.endTime),
                            firstTuple.get(notification.intervalMinutes),
                            firstTuple.get(scrap.title),
                            firstTuple.get(6, Long.class),
                            topicTypes
                        );
                    })))
            .values()
            .stream()
            .limit(pageSize)
            .toList();

        boolean hasNext = results.size() > pageSize;
        Long nextContentId = hasNext ? contents.get(contents.size() - 1).notificationId() : null;

        return new NotificationsResponse(contents, nextContentId, nextNotificationId == 1,
            !hasNext);
    }
}
