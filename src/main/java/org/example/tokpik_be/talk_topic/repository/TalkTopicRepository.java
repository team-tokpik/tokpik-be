package org.example.tokpik_be.talk_topic.repository;

import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TalkTopicRepository extends JpaRepository<TalkTopic, Long> {

}
