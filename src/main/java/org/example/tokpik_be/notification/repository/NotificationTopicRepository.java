package org.example.tokpik_be.notification.repository;

import org.example.tokpik_be.notification.domain.NotificationTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationTopicRepository extends JpaRepository<NotificationTopic, Long> {

}
