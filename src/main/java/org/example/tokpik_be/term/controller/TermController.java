package org.example.tokpik_be.term.controller;

import java.util.List;

import org.example.tokpik_be.term.dto.response.TermResponse;
import org.example.tokpik_be.term.service.TermService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TermController {

    private final TermService termService;

    @Operation(summary = "이용약관 조회", description = "이용약관 조회")
    @ApiResponse(responseCode = "200", description = "이용약관 조회 성공")
    @GetMapping("/terms")
    public ResponseEntity<List<TermResponse>> getAllTerm(){
        List<TermResponse> terms = termService.getAllTerms();
        return ResponseEntity.ok(terms);
    }
}
