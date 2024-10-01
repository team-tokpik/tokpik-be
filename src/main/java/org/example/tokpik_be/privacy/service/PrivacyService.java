package org.example.tokpik_be.privacy.service;

import java.util.List;

import org.example.tokpik_be.privacy.domain.Privacy;
import org.example.tokpik_be.privacy.dto.response.PrivacyResponse;
import org.example.tokpik_be.privacy.repository.PrivacyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrivacyService {

    private final PrivacyRepository privacyRepository;

    @Transactional(readOnly = true)
    public List<PrivacyResponse> getAllPrivacy() {
        List<Privacy> privacies = privacyRepository.findAll();
        return privacies.stream()
            .map(privacy -> new PrivacyResponse(privacy.getTitle(), privacy.getContent()))
            .toList();
    }
}
