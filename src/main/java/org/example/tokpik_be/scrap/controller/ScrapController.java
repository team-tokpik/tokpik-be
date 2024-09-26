package org.example.tokpik_be.scrap.controller;

import org.example.tokpik_be.scrap.dto.response.ScrapListResponse;
import org.example.tokpik_be.scrap.service.ScrapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Scrap API", description = "스크랩 관련 API")
@RestController
@RequiredArgsConstructor
public class ScrapController {

    private final ScrapService scrapService;

    @GetMapping("/users/scraps")
    @Operation(summary = "스크랩 리스트 조회", description = "스크랩 리스트 조회")
    @ApiResponse(responseCode = "200", description = "스크랩 리스트 조회 성공")
    public ResponseEntity<ScrapListResponse> getScrapList(
        @RequestAttribute("userId") long userId) {
        ScrapListResponse scrapListResponse = scrapService.getScrapList(userId);
        return ResponseEntity.ok(scrapListResponse);
    }
}
