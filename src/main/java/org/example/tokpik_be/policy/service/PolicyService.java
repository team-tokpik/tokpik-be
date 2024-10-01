package org.example.tokpik_be.policy.service;

import java.util.List;

import org.example.tokpik_be.policy.domain.Policy;
import org.example.tokpik_be.policy.dto.response.PolicyResponse;
import org.example.tokpik_be.policy.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;

    @Transactional(readOnly = true)
    public List<PolicyResponse> getAllPolicy() {
        List<Policy> policys = policyRepository.findAll();
        return policys.stream()
            .map(privacy -> new PolicyResponse(privacy.getTitle(), privacy.getContent()))
            .toList();
    }
}
