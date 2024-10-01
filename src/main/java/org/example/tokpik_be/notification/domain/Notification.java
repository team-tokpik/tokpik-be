package org.example.tokpik_be.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tokpik_be.common.BaseTimeEntity;
import org.example.tokpik_be.scrap.domain.Scrap;
import org.example.tokpik_be.user.domain.User;

@Table(name = "notifications")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate noticeDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    private int intervalMinutes;
    private boolean deleted;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "scrap_id")
    private Scrap scrap;

    @OneToMany
    @JoinColumn(name = "notification_id")
    private List<NotificationTalkTopic> notificationTalkTopics = new ArrayList<>();

    public Notification(LocalDate noticeDate,
        LocalTime startTime,
        LocalTime endTime,
        int intervalMinutes,
        User user,
        Scrap scrap) {
        this.noticeDate = noticeDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalMinutes = intervalMinutes;
        this.deleted = false;
        this.user = user;
        this.scrap = scrap;
    }

    public void delete() {
        this.deleted = true;
    }

    public void addNotificationTalkTopics(List<NotificationTalkTopic> notificationTalkTopics) {
        this.notificationTalkTopics.addAll(notificationTalkTopics);
    }
}
