package org.example.tokpik_be.scrap.repository;

import java.util.List;

import org.example.tokpik_be.scrap.domain.Scrap;
import org.example.tokpik_be.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    List<Scrap> findByUserIdOrderByCreatedAtDesc(Long id);

    Long countByUser(User user);

    boolean existsByScrapIdAndTalkTopicId(Long scrapId, Long topicId);
}
