package org.example.tokpik_be.scrap.controller;

import org.example.tokpik_be.scrap.dto.response.ScrapCountResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.scrap.dto.request.ScrapCreateRequest;
import org.example.tokpik_be.scrap.dto.response.ScrapCreateResponse;
import org.example.tokpik_be.scrap.dto.response.ScrapListResponse;
import org.example.tokpik_be.scrap.service.ScrapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
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
}
