package org.example.tokpik_be.tag.repository;

import java.util.List;

import org.example.tokpik_be.tag.domain.UserTopicTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTopicTagRepository extends JpaRepository<UserTopicTag, Long> {

    List<UserTopicTag> findByUserId(Long userId);

    void deleteByUserId(Long id);
}
