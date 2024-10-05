package org.example.tokpik_be.tag.repository;

import java.util.List;

import org.example.tokpik_be.tag.domain.UserPlaceTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPlaceTagRepository extends JpaRepository<UserPlaceTag, Long> {

    List<UserPlaceTag> findByUserId(Long userId);

    void deleteByUserId(Long id);
}
