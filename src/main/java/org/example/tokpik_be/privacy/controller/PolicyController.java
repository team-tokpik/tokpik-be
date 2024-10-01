package org.example.tokpik_be.privacy.controller;

import java.util.List;

import org.example.tokpik_be.privacy.dto.response.PolicyResponse;
import org.example.tokpik_be.privacy.service.PolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "개인정보정책 조회 API", description = "개인정보정책 연관 API")
@RestController
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService privacyService;

    @Operation(summary = "개인정보정책 조회", description = "개인정보정책 조회")
    @ApiResponse(responseCode = "200", description = "개인정보정책 조회 성공")
    @GetMapping("/policys")
    public ResponseEntity<List<PolicyResponse>> getAllPolicys() {
        List<PolicyResponse> policys = privacyService.getAllPolicy();
        return ResponseEntity.ok(policys);
    }

}
