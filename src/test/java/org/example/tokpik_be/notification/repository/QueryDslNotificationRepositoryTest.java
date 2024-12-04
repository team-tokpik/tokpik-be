package org.example.tokpik_be.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.example.tokpik_be.config.QueryDslConfig;
import org.example.tokpik_be.notification.domain.Notification;
import org.example.tokpik_be.notification.domain.NotificationTalkTopic;
import org.example.tokpik_be.notification.dto.response.NotificationScheduledResponse;
import org.example.tokpik_be.notification.dto.response.NotificationsResponse;
import org.example.tokpik_be.notification.dto.response.NotificationsResponse.NotificationResponse;
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

@ActiveProfiles("test")
@Import(QueryDslConfig.class)
@DataJpaTest
class QueryDslNotificationRepositoryTest {

    private final String title = "제목";
    private final String subtitle = "부제목";
    private final String situation = "대화분위기";
    TalkPartner talkPartner = new TalkPartner(Gender.FEMALE, 20, 30);

    private User user;
    private TopicType topicType;
    private PlaceType placeType;

    @Autowired
    private EntityManager em;

    @Autowired
    private JPAQueryFactory queryFactory;

    private QueryDslNotificationRepository queryDslNotificationRepository;

    @BeforeEach
    void setUp() {
        this.queryDslNotificationRepository = new QueryDslNotificationRepository(queryFactory);

        user = new User("ex@example.com", "https://kakao.com/profile-photo/1");
        user.updateNotificationToken("header.payload.signature");
        em.persist(user);
        em.flush();

        topicType = new TopicType("비즈니스");
        em.persist(topicType);
        em.flush();

        placeType = new PlaceType("회사");
        em.persist(placeType);
        em.flush();
    }

    @DisplayName("알림 목록을 조회하는 데 성공한다.")
    @Test
    void getNotifications() {
        // given
        List<TalkTopic> talkTopics = setupTalkTopics(12);
        Scrap scrap = setupScrap();
        setupScrapTopics(scrap, talkTopics);

        LocalDateTime now = LocalDateTime.now();
        LocalDate noticeDate = now.toLocalDate();
        LocalTime startTime = now.toLocalTime();
        LocalTime endTime = startTime.plusMinutes(30);
        int intervalMinutes = 10;

        String notificationName = "알림";
        List<Notification> notifications = IntStream.range(1, 4)
            .mapToObj(i -> new Notification(notificationName, noticeDate, startTime, endTime,
                intervalMinutes, user, scrap))
            .toList();
        notifications.forEach(notification -> em.persist(notification));
        em.flush();

        List<NotificationTalkTopic> totalNotificationTalkTopics = new ArrayList<>();
        int idx = 0;
        for (Notification notification : notifications) {
            long notificationId = notification.getId();
            List<NotificationTalkTopic> notificationTalkTopics = talkTopics.subList(idx, idx + 4)
                .stream()
                .map(talkTopic -> new NotificationTalkTopic(notificationId, talkTopic))
                .toList();
            totalNotificationTalkTopics.addAll(notificationTalkTopics);

            idx += 4;
        }
        totalNotificationTalkTopics
            .forEach(notificationTalkTopic -> em.persist(notificationTalkTopic));
        em.flush();

        // when
        NotificationsResponse response = queryDslNotificationRepository
            .getNotifications(user.getId(), null, 3);

        // then
        List<NotificationResponse> contents = response.contents();
        assertThat(contents).hasSize(3);
        assertThat(response.nextCursorId()).isNull();
        assertThat(response.first()).isTrue();
        assertThat(response.last()).isTrue();

        List<Long> notificationIds = notifications.stream().map(Notification::getId).toList();
        List<Long> responseNotificationIds = contents.stream()
            .map(NotificationResponse::notificationId).toList();
        assertThat(responseNotificationIds).containsAll(notificationIds);

        assertThat(contents).allSatisfy(notificationResponse -> {
            assertThat(notificationResponse.noticeDate()).isEqualTo(noticeDate);
            assertThat(notificationResponse.notificationInterval()).isEqualTo(intervalMinutes);
            assertThat(notificationResponse.notificationName()).isEqualTo(notificationName);
            assertThat(notificationResponse.notificationTopicTotal()).isEqualTo(4);
            assertThat(notificationResponse.notificationTalkTopicTypes()).hasSize(4);
        });
    }

    private List<TalkTopic> setupTalkTopics(int count) {
        List<TalkTopic> talkTopics = IntStream.range(0, count).mapToObj(
                i -> new TalkTopic(title, subtitle, situation, talkPartner, topicType, placeType))
            .toList();
        talkTopics.forEach(talkTopic -> em.persist(talkTopic));
        em.flush();

        return talkTopics;
    }

    private Scrap setupScrap() {
        Scrap scrap = new Scrap("스크랩 제목", user);
        em.persist(scrap);
        em.flush();

        return scrap;
    }

    private void setupScrapTopics(Scrap scrap, List<TalkTopic> talkTopics) {
        List<ScrapTopic> scrapTopics = talkTopics.stream()
            .map(talkTopic -> new ScrapTopic(scrap, talkTopic))
            .toList();
        scrapTopics.forEach(scrapTopic -> em.persist(scrapTopic));
        em.flush();
    }

    @DisplayName("예정된 알림 목록을 조회하는 데 성공한다.")
    @Test
    void getScheduledNotifications() {
        // given
        List<TalkTopic> talkTopics = setupTalkTopics(10);
        Scrap scrap = setupScrap();
        setupScrapTopics(scrap, talkTopics);

        String notificationName = "알림";
        LocalDateTime now = LocalDateTime.now();
        LocalDate noticeDate = now.toLocalDate();
        LocalTime noticeTime = now.toLocalTime();
        LocalTime startTime = noticeTime.minusMinutes(100);
        LocalTime endTime = noticeTime.plusMinutes(100);
        int intervalMinutes = 10;
        Notification notification = new Notification(notificationName, noticeDate, startTime,
            endTime, intervalMinutes, user, scrap);
        em.persist(notification);
        em.flush();

        long notificationId = notification.getId();
        List<NotificationTalkTopic> notificationTalkTopics = talkTopics.stream()
            .map(talkTopic -> new NotificationTalkTopic(notificationId, talkTopic))
            .toList();
        notificationTalkTopics.forEach(notificationTalkTopic -> em.persist(notificationTalkTopic));
        em.flush();

        // when
        List<NotificationScheduledResponse> scheduledNotifications = queryDslNotificationRepository
            .getScheduledNotifications(now);

        // then
        assertThat(scheduledNotifications).hasSize(10);

        assertThat(scheduledNotifications).allSatisfy(scheduledNotification -> {
            assertThat(scheduledNotification.notificationId()).isEqualTo(notificationId);
            assertThat(scheduledNotification.receiverToken())
                .isEqualTo(user.getNotificationToken());
            assertThat(scheduledNotification.talkTopicTitle()).isEqualTo(title);
            assertThat(scheduledNotification.talkTopicSubtitle()).isEqualTo(subtitle);
            assertThat(areTimesEqualToSecond(scheduledNotification.startTime(), startTime))
                .isTrue();
            assertThat(areTimesEqualToSecond(scheduledNotification.endTime(), endTime))
                .isTrue();
        });

        List<Long> notificationTalkTopicIds = notificationTalkTopics.stream()
            .map(NotificationTalkTopic::getId)
            .toList();
        List<Long> scheduledNotificationTalkTopicIds = scheduledNotifications.stream()
            .map(NotificationScheduledResponse::notificationTalkTopicId)
            .toList();
        assertThat(scheduledNotificationTalkTopicIds).containsAll(notificationTalkTopicIds);
    }

    private boolean areTimesEqualToSecond(LocalTime time1, LocalTime time2) {

        return time1.truncatedTo(ChronoUnit.SECONDS).equals(time2.truncatedTo(ChronoUnit.SECONDS));
    }
}
