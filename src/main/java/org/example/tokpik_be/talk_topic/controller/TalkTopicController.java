package org.example.tokpik_be.talk_topic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.talk_topic.dto.request.TalkTopicSearchRequest;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicsSearchResponse;
import org.example.tokpik_be.talk_topic.service.TalkTopicCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "대화 주제 API", description = "대화 주제 연관 API")
@RestController
@RequiredArgsConstructor
public class TalkTopicController {

    private final TalkTopicCommandService talkTopicCommandService;

    @Operation(summary = "대화 주제 조회(검색)", description = "대화 주제 조회(검색) API")
    @ApiResponse(responseCode = "200" ,description = "대화 주제 검색 성공")
    @PostMapping("/topics")
    public ResponseEntity<TalkTopicsSearchResponse> searchTalkTopics(
        @RequestAttribute("userId") long userId,
        @RequestBody TalkTopicSearchRequest request) {

        TalkTopicsSearchResponse response = talkTopicCommandService
            .generateTopics(userId, request);

        return ResponseEntity.ok().body(response);
    }
}
