package org.example.tokpik_be.tag.repository;

import java.util.List;

import org.example.tokpik_be.tag.entity.UserTopicTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTopicTagRepository extends JpaRepository<UserTopicTag, Long> {

    List<UserTopicTag> findByUserId(Long userId);
}
