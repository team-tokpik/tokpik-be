package org.example.tokpik_be.scrap.repository;

import org.example.tokpik_be.scrap.domain.ScrapTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapTopicRepository extends JpaRepository<ScrapTopic, Long> {

}
