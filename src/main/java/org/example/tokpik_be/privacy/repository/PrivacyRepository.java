package org.example.tokpik_be.privacy.repository;

import org.example.tokpik_be.privacy.domain.Privacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivacyRepository extends JpaRepository<Privacy, Long> {
}
