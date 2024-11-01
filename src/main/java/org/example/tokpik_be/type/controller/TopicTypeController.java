package org.example.tokpik_be.type.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.type.dto.request.UserTopicTypesRequest;
import org.example.tokpik_be.type.dto.response.TopicTypeTotalResponse;
import org.example.tokpik_be.type.dto.response.UserTopicTypeResponse;
import org.example.tokpik_be.type.service.TopicTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "대화 타입 API", description = "대화 종류 연관 API")
@RestController
@RequiredArgsConstructor
public class TopicTypeController {

    private final TopicTypeService topicTypeService;

    @GetMapping("/users/types")
    @Operation(summary = "사용자 대화 타입 조회", description = "내 대화 타입 조회")
    @ApiResponse(responseCode = "200", description = "사용자 대화 태그 타입 성공")
    public ResponseEntity<UserTopicTypeResponse> getUserTopicTypes(
        @RequestAttribute("userId") long userId) {

        UserTopicTypeResponse response = topicTypeService.getUserTopicTypes(userId);

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/users/types")
    @Operation(summary = "사용자 대화 타입 수정", description = "대화 타입 추가 또는 제거, 1개 이상의 타입 선택 필요")
    @ApiResponse(responseCode = "200", description = "사용자 대화 타입 수정 성공")
    public ResponseEntity<UserTopicTypeResponse> updateUserTopicTypes(
        @RequestAttribute("userId") long userId,
        @RequestBody @Valid UserTopicTypesRequest request) {

        UserTopicTypeResponse response = topicTypeService.updateUserTopicTypes(userId, request);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "전체 대화 타입 조회", description = "전체 대화 타입 조회")
    @ApiResponse(responseCode = "200", description = "전체 대화 타입 조회 성공")
    @GetMapping("/topic-types")
    public ResponseEntity<TopicTypeTotalResponse> getAllTopicTypes() {

        TopicTypeTotalResponse response = topicTypeService.getAllTopicTypes();

        return ResponseEntity.ok().body(response);
    }
}
