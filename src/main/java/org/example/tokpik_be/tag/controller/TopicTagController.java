package org.example.tokpik_be.tag.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.tag.dto.request.UserTopicTagsRequest;
import org.example.tokpik_be.tag.dto.response.TopicTagTotalResponse;
import org.example.tokpik_be.tag.dto.response.UserTopicTagResponse;
import org.example.tokpik_be.tag.service.TopicTagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "대화 태그 API", description = "대화 종류 연관 API")
@RestController
@RequiredArgsConstructor
public class TopicTagController {

    private final TopicTagService topicTagService;

    @GetMapping("/users/tags")
    @Operation(summary = "사용자 대화 태그 조회", description = "내 대화 태그 조회")
    @ApiResponse(responseCode = "200", description = "사용자 대화 태그 조회 성공")
    public ResponseEntity<UserTopicTagResponse> getUserTopicTags(
        @RequestAttribute("userId") long userId) {

        UserTopicTagResponse response = topicTagService.getUserTopicTags(userId);

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/users/tags")
    @Operation(summary = "사용자 대화 태그 수정", description = "대화 태그 추가 또는 제거, 1개 이상의 태그 선택 필요")
    @ApiResponse(responseCode = "200", description = "사용자 대화 태그 수정 성공")
    public ResponseEntity<UserTopicTagResponse> updateUserTopicTags(
        @RequestAttribute("userId") long userId,
        @RequestBody @Valid UserTopicTagsRequest request) {

        UserTopicTagResponse response = topicTagService.updateUserTopicTags(userId, request);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "전체 대화 종류 조회", description = "전체 대화 종류 조회")
    @ApiResponse(responseCode = "200", description = "전체 대화 종류 조회 성공")
    @GetMapping("/topic-tags")
    public ResponseEntity<TopicTagTotalResponse> getAllTopicTags() {

        TopicTagTotalResponse response = topicTagService.getAllTopicTags();

        return ResponseEntity.ok().body(response);
    }
}
