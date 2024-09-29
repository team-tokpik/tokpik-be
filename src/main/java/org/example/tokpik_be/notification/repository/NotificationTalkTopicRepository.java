package org.example.tokpik_be.notification.repository;

import java.util.List;
import org.example.tokpik_be.notification.domain.NotificationTalkTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationTalkTopicRepository
    extends JpaRepository<NotificationTalkTopic, Long> {

    List<NotificationTalkTopic> findAllByNotificationIdIn(List<Long> notificationIds);
}
