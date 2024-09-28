package org.example.tokpik_be.scrap.repository;

import java.util.List;

import org.example.tokpik_be.scrap.domain.Scrap;
import org.example.tokpik_be.scrap.domain.ScrapTopic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapTopicRepository extends JpaRepository<ScrapTopic, Long> {

    List<ScrapTopic> findByScrapOrderByCreatedAtDesc(Scrap scrap);

    @Query("SELECT COUNT(st) FROM ScrapTopic st WHERE st.scrap.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    List<ScrapTopic> findByScrapIdAndIdGreaterThanOrderByIdAsc(Long scrapId, Long lastContentId, Pageable pageable);

    boolean existsByScrapIdAndIdGreaterThan(Long scrapId, Long newLastContentId);

    long countByScrapIdAndIdGreaterThan(Long scrapId, Long lastContentId);

    boolean existsByScrapIdAndId(Long scrapId, Long topicId);
}
