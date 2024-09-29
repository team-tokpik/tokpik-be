package org.example.tokpik_be.notification.repository;

import java.util.List;
import org.example.tokpik_be.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByDeletedIsTrue();
}
