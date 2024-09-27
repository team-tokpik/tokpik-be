package org.example.tokpik_be.notification.repository;

import java.util.List;
import org.example.tokpik_be.notification.domain.Notification20Min;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Notification20MinRepository extends JpaRepository<Notification20Min, Long> {

    List<Notification20Min> findAllByDeletedIsTrue();
}
