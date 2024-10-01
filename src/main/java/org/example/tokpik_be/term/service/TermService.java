package org.example.tokpik_be.term.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.tokpik_be.term.domain.Term;
import org.example.tokpik_be.term.dto.response.TermResponse;
import org.example.tokpik_be.term.repository.TermRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TermService {

    private final TermRepository termRepository;

    @Transactional(readOnly = true)
    public List<TermResponse> getAllTerms() {
        List<Term> terms = termRepository.findAll();

        Map<String, List<Term>> groupedTerms = terms.stream()
            .collect(Collectors.groupingBy(Term::getTitle));

        return groupedTerms.entrySet().stream()
            .map(entry -> new TermResponse(
                entry.getKey(),
                entry.getValue().stream()
                    .map(term -> new TermResponse.TermsSection(
                        term.getMainCategory().toString(),
                        term.getSubCategory().toString(),
                        term.getContentTitle(),
                        term.getContent()
                    ))
                    .collect(Collectors.toList())
            ))
            .toList();
    }

}
