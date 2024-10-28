package org.example.tokpik_be.type.repository;

import java.util.List;

import org.example.tokpik_be.type.domain.UserTopicType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTopicTypeRepository extends JpaRepository<UserTopicType, Long> {

    List<UserTopicType> findByUserId(Long userId);

    void deleteByUserId(Long id);
}
