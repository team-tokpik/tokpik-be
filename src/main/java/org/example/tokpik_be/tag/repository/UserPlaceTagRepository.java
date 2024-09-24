package org.example.tokpik_be.tag.repository;

import org.example.tokpik_be.tag.entity.UserPlaceTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPlaceTagRepository extends JpaRepository<UserPlaceTag, Long> {

}
