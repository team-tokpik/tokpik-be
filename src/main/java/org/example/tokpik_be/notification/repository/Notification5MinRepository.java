package org.example.tokpik_be.notification.repository;

import org.example.tokpik_be.notification.domain.Notification5Min;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Notification5MinRepository extends JpaRepository<Notification5Min, Long> {

}
