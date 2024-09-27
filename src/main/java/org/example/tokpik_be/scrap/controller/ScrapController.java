package org.example.tokpik_be.scrap.controller;

import org.example.tokpik_be.scrap.dto.response.ScrapCountResponse;
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

    @Operation(summary = "스크랩 리스트 조회", description = "스크랩 리스트 조회")
    @ApiResponse(responseCode = "200", description = "스크랩 리스트 조회 성공")
    @GetMapping("/users/scraps")
    public ResponseEntity<ScrapListResponse> getScrapList(
        @RequestAttribute("userId") long userId) {
        ScrapListResponse scrapListResponse = scrapService.getScrapList(userId);
        return ResponseEntity.ok(scrapListResponse);
    }

    @Operation(summary = "총 스크랩 수 조회", description = "총 스크랩 수 조회")
    @ApiResponse(responseCode = "200", description = "스크랩 수 조회 성공")
    @GetMapping("/users/scraps/count")
    public ResponseEntity<ScrapCountResponse> getScrapCounts(
        @RequestAttribute("userId") long userId){
        ScrapCountResponse scrapCountResponse = scrapService.getUserSrcapCounts(userId);
        return ResponseEntity.ok(scrapCountResponse);
    }


    @Operation(summary = "총 스크랩 톡픽 수 조회", description = "사용자 총 스크랩 톡픽 수 조회")
    @ApiResponse(responseCode = "200", description = "스크랩 톡픽 수 조회 성공")
    @GetMapping("/users/scraps/topik/count")
    public ResponseEntity<ScrapCountResponse> getTopicCounts(
        @RequestAttribute("userId") long userId){
        ScrapCountResponse scrapCountResponse = scrapService.getUserTopicCounts(userId);
        return ResponseEntity.ok(scrapCountResponse);
    }
}
