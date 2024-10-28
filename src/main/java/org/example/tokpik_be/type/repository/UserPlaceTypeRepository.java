package org.example.tokpik_be.type.repository;

import java.util.List;

import org.example.tokpik_be.type.domain.UserPlaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPlaceTypeRepository extends JpaRepository<UserPlaceType, Long> {

    List<UserPlaceType> findByUserId(Long userId);

    void deleteByUserId(Long id);
}
