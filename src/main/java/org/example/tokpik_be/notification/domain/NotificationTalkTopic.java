package org.example.tokpik_be.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;

@Table(name = "notification_talk_topics")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationTalkTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_id")
    private long notificationId;

    @ManyToOne
    @JoinColumn(name = "talk_topic_id")
    private TalkTopic talkTopic;
}
