package org.example.tokpik_be.tag.controller;

import org.example.tokpik_be.tag.dto.request.UserPlaceTagsRequest;
import org.example.tokpik_be.tag.dto.response.UserPlaceTagResponse;
import org.example.tokpik_be.tag.service.PlaceTagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PlaceTagController {

    private final PlaceTagService placeTagService;

    @GetMapping("/users/places")
    @Operation(summary = "사용자 장소 태그 조회", description = "내 장소 태그 조회")
    @ApiResponse(responseCode = "200", description = "사용자 장소 태그 조회 성공")
    public ResponseEntity<UserPlaceTagResponse> getUserPlaceTags(
        @RequestAttribute("userId") long userId) {

        UserPlaceTagResponse response = placeTagService.getUserPlaceTags(userId);

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/users/places")
    @Operation(summary = "사용자 장소 태그 수정", description = "장소 태그 수정, 1개 이상의 태그 선택 필요")
    @ApiResponse(responseCode = "200", description = "사용자 장소 태그 수정 성공")
    public ResponseEntity<UserPlaceTagResponse> updateUserPlaceTags(
        @RequestAttribute("userId") long userId,
        @RequestBody @Valid UserPlaceTagsRequest request) {

        UserPlaceTagResponse response = placeTagService.updateUserPlaceTags(userId, request);

        return ResponseEntity.ok().body(response);
    }
}
