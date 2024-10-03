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
        List<Policy> policies = policyRepository.findAll();
        return policies.stream()
            .map(policy -> new PolicyResponse(policy.getTitle(), policy.getContent()))
            .toList();
    }
}
