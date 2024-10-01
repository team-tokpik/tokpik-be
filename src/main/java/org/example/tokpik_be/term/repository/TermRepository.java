package org.example.tokpik_be.term.repository;

import org.example.tokpik_be.term.domain.Term;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermRepository extends JpaRepository<Term, Long> {

}
