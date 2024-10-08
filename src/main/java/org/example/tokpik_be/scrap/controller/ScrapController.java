package org.example.tokpik_be.scrap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.scrap.dto.request.ScrapCreateRequest;
import org.example.tokpik_be.scrap.dto.request.ScrapUpdateTitleRequest;
import org.example.tokpik_be.scrap.dto.response.ScrapCountResponse;
import org.example.tokpik_be.scrap.dto.response.ScrapCreateResponse;
import org.example.tokpik_be.scrap.dto.response.ScrapListResponse;
import org.example.tokpik_be.scrap.dto.response.ScrapResponse;
import org.example.tokpik_be.scrap.service.ScrapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "스크랩 API", description = "스크랩 연관 API")
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
        @RequestAttribute("userId") long userId) {
        ScrapCountResponse scrapCountResponse = scrapService.getUserSrcapCounts(userId);
        return ResponseEntity.ok(scrapCountResponse);
    }

    @Operation(summary = "총 스크랩 톡픽 수 조회", description = "사용자 총 스크랩 톡픽 수 조회")
    @ApiResponse(responseCode = "200", description = "스크랩 톡픽 수 조회 성공")
    @GetMapping("/users/scraps/topics/count")
    public ResponseEntity<ScrapCountResponse> getTopicCounts(
        @RequestAttribute("userId") long userId) {
        ScrapCountResponse scrapCountResponse = scrapService.getUserTopicCounts(userId);
        return ResponseEntity.ok(scrapCountResponse);
    }

    @Operation(summary = "스크랩 생성", description = "스크랩 생성")
    @ApiResponse(responseCode = "200", description = "스크랩 생성 성공")
    @PostMapping("/users/scraps")
    public ResponseEntity<ScrapCreateResponse> createScrap(@RequestAttribute("userId") long userId,
        @RequestBody @Valid ScrapCreateRequest scrapCreateRequest) {

        ScrapCreateResponse response = scrapService.createScrap(userId, scrapCreateRequest);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "대화 주제 스크랩", description = "대화 주제 스크랩")
    @ApiResponse(responseCode = "200", description = "대화 주제 스크랩 성공")
    @PostMapping("/users/scraps/{scrapId}/topics/{topicId}")
    public ResponseEntity<Void> scrapTopic(
        @Parameter(name = "scrapId", description = "스크랩 ID", example = "1", in = ParameterIn.PATH)
        @PathVariable("scrapId") long scrapId,

        @Parameter(name = "topicId", description = "스크랩할 대화 주제 ID", example = "1", in = ParameterIn.PATH)
        @PathVariable("topicId") long topicId) {

        scrapService.scrapTopic(scrapId, topicId);

        return ResponseEntity.ok().build();

    }

    @Operation(summary = "스크랩 삭제", description = "스크랩 삭제")
    @ApiResponse(responseCode = "200", description = "스크랩 삭제 성공")
    @DeleteMapping("/users/scraps/{scrapId}")
    public ResponseEntity<Void> deleteScrap(
        @Parameter(name = "scrapId", description = "스크랩 ID", example = "1", in = ParameterIn.PATH)
        @PathVariable("scrapId") long scrapId) {

        scrapService.deleteScrap(scrapId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "스크랩 대화 주제 삭제", description = "스크랩한 대화 주제를 삭제")
    @ApiResponse(responseCode = "200", description = "대화 주제 삭제 성공")
    @DeleteMapping("/users/scraps/{scrapId}/topics/{scrapTopicId}")
    public ResponseEntity<Void> deleteScrapTopic(
        @Parameter(name = "scrapId", description = "스크랩 ID", example = "1", in = ParameterIn.PATH)
        @PathVariable long scrapId,

        @Parameter(name = "scrapTopicId", description = "삭제할 스크랩 대화 주제 ID", example = "1", in = ParameterIn.PATH)
        @PathVariable long scrapTopicId) {

        scrapService.deleteScrapTopic(scrapId, scrapTopicId);

        return ResponseEntity.ok().build();
    }


    @Operation(summary = "스크랩 조회", description = "특정 스크랩에 포함된 대화 주제들을 조회")
    @ApiResponse(responseCode = "200", description = "스크랩 조회 성공")
    @GetMapping("/users/scraps/{scrapId}/topics")
    public ResponseEntity<ScrapResponse> getScrapTopics(
        @PathVariable @Parameter(description = "스크랩 ID") Long scrapId,
        @RequestParam(defaultValue = "0")
        @Parameter(description = "마지막 스크랩된 주제 ID, 커서 페이징에 사용") Long nextCursorId,
        @RequestParam(defaultValue = "10")
        @Parameter(description = "페이지 크기") int size
    ) {
        ScrapResponse response = scrapService.getScrapTopics(scrapId, nextCursorId, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "스크랩 제목 수정", description = "스크랩 제목 수정")
    @ApiResponse(responseCode = "200", description = "스크랩 제목 수정 성공")
    @PatchMapping("/users/scraps/{scrapId}/titles")
    public ResponseEntity<Void> updateScrapTitle(@RequestAttribute("userId") long userId,
        @Parameter(name = "scrapId", description = "스크랩 ID", example = "1")
        @PathVariable("scrapId") long scrapId,

        @RequestBody @Valid ScrapUpdateTitleRequest request) {

        scrapService.updateScrapTitle(userId, scrapId, request);

        return ResponseEntity.ok().build();
    }
}
