package org.example.tokpik_be.tag.repository;

import org.example.tokpik_be.tag.domain.TopicTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicTagRepository extends JpaRepository<TopicTag, Long> {

}
