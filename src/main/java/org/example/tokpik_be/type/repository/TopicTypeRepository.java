package org.example.tokpik_be.type.repository;

import org.example.tokpik_be.type.domain.TopicType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicTypeRepository extends JpaRepository<TopicType, Long> {

}
