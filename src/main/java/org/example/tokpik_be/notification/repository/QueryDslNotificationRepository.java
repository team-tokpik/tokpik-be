package org.example.tokpik_be.notification.repository;

import static org.example.tokpik_be.notification.domain.QNotification.notification;
import static org.example.tokpik_be.notification.domain.QNotificationTalkTopic.notificationTalkTopic;
import static org.example.tokpik_be.scrap.domain.QScrap.scrap;
import static org.example.tokpik_be.tag.domain.QTopicTag.topicTag;
import static org.example.tokpik_be.talk_topic.domain.QTalkTopic.talkTopic;
import static org.example.tokpik_be.user.domain.QUser.user;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.notification.dto.response.NotificationScheduledResponse;
import org.example.tokpik_be.notification.dto.response.NotificationsResponse;
import org.example.tokpik_be.notification.dto.response.NotificationsResponse.NotificationResponse;
import org.example.tokpik_be.notification.dto.response.NotificationsResponse.NotificationResponse.NotificationTalkTopicTypeResponse;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslNotificationRepository {

    private final JPAQueryFactory queryFactory;

    public NotificationsResponse getNotifications(long userId, Long cursorId, int pageSize) {
        // 사용자 보유 알림 확인 where 조건 정의
        BooleanExpression usersNotificationCondition = notification.user.id.eq(userId);

        // 첫 페이지 여부 도출
        boolean first = Optional.ofNullable(cursorId).isEmpty();

        // 다음 페이지 존재 확인 where 조건 정의
        BooleanExpression nextPageCondition = Optional.ofNullable(cursorId)
            .map(id -> notification.id.gt(cursorId))
            .orElse(null);

        // 대상 알림 ID 목록 조회
        List<Long> notificationIds = queryFactory.select(notification.id)
            .from(notification)
            .where(usersNotificationCondition.and(nextPageCondition)
                .and(notification.deleted.isFalse()))
            .limit(pageSize + 1)
            .fetch();

        // 마지막 페이지 여부 도출
        boolean last = notificationIds.size() <= pageSize;
        Long nextCursorId = null;

        // pageSize + 1개 조회시 마지막 항목 제거, 다음 커서 ID 설정
        if (!last) {
            nextCursorId = notificationIds.get(notificationIds.size() - 1);
            notificationIds = notificationIds.subList(0, pageSize + 1);
        }

        // 알림과 알림 포함된 대화 주제들의 대화 종류 데이터 조회
        List<Tuple> results = queryFactory.from(notification)
            .select(notification.id,
                notification.noticeDate,
                notification.startTime,
                notification.endTime,
                notification.intervalMinutes,
                notificationTalkTopic.id,
                scrap.title,
                topicTag.id,
                topicTag.content)
            .join(notification.notificationTalkTopics, notificationTalkTopic)
            .join(notification.scrap, scrap)
            .join(notificationTalkTopic.talkTopic, talkTopic)
            .join(talkTopic.topicTag, topicTag)
            .where(usersNotificationCondition.and(notification.id.in(notificationIds)))
            .fetch();

        // 알림 ID별로 조회 데이터들 grouping
        Map<Long, List<Tuple>> groupByNotificationId = results.stream()
            .collect(Collectors.groupingBy(result -> result.get(notification.id)));

        // group 바탕으로 알림별 알림 대화 주제 ID(알림 지정 순서)에 따라 정렬, 첫 세 알림 대화 주제 선정, 응답 DTO로 매핑
        List<NotificationResponse> contents = groupByNotificationId.entrySet().stream()
            .map(entry -> {
                long notificationId = entry.getKey();
                List<Tuple> tuples = entry.getValue();

                long notificationTopicTotal = tuples.size();

                List<NotificationTalkTopicTypeResponse> talkTopicTypeResponses = tuples
                    .stream()
                    .sorted(Comparator.comparing(r -> r.get(notificationTalkTopic.id)))
                    .toList().subList(0, 3)
                    .stream()
                    .map(r -> new NotificationTalkTopicTypeResponse(r.get(topicTag.id),
                        r.get(topicTag.content)))
                    .toList();

                Tuple tuple = tuples.get(0);

                return new NotificationResponse(notificationId,
                    tuple.get(notification.noticeDate),
                    tuple.get(notification.startTime),
                    tuple.get(notification.endTime),
                    tuple.get(notification.intervalMinutes),
                    tuple.get(scrap.title),
                    notificationTopicTotal,
                    talkTopicTypeResponses);
            })
            .sorted(Comparator.comparingLong(NotificationResponse::notificationId))
            .toList();

        return new NotificationsResponse(contents, nextCursorId, first, last);
    }

    public List<NotificationScheduledResponse> getScheduledNotifications(LocalDate noticeDate,
        LocalTime from,
        LocalTime to,
        int intervalMinutes) {

        return queryFactory.from(notification)
            .select(Projections.constructor(NotificationScheduledResponse.class,
                notification.user.notificationToken,
                talkTopic.title,
                talkTopic.subtitle))
            .join(notification.user, user)
            .join(notification.notificationTalkTopics, notificationTalkTopic)
            .join(notificationTalkTopic.talkTopic, talkTopic)
            .where(notification.noticeDate.eq(noticeDate)
                .and(notification.startTime.goe(from))
                .and(notification.endTime.loe(to))
                .and(notification.intervalMinutes.eq(intervalMinutes)))
            .fetch();
    }
}

