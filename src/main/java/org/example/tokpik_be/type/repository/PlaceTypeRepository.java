package org.example.tokpik_be.type.repository;

import org.example.tokpik_be.type.domain.PlaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceTypeRepository extends JpaRepository<PlaceType, Long> {

}
