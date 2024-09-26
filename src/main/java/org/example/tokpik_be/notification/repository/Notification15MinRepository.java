package org.example.tokpik_be.notification.repository;

import org.example.tokpik_be.notification.domain.Notification15Min;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Notification15MinRepository extends JpaRepository<Notification15Min, Long> {

}
