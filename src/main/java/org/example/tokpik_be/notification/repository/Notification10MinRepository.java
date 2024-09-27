package org.example.tokpik_be.notification.repository;

import java.util.List;
import org.example.tokpik_be.notification.domain.Notification10Min;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Notification10MinRepository extends JpaRepository<Notification10Min, Long> {

    List<Notification10Min> findAllByDeletedIsTrue();
}
