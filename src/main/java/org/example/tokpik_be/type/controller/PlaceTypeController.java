package org.example.tokpik_be.type.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.tokpik_be.type.dto.request.UserPlaceTypesRequest;
import org.example.tokpik_be.type.dto.response.PlaceTypeTotalResponse;
import org.example.tokpik_be.type.dto.response.UserPlaceTypeResponse;
import org.example.tokpik_be.type.service.PlaceTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "대화 장소 타입 API", description = "대화 장소 타입 연관 API")
@RestController
@RequiredArgsConstructor
public class PlaceTypeController {

    private final PlaceTypeService placeTypeService;

    @GetMapping("/users/place-types")
    @Operation(summary = "사용자 장소 타입 조회", description = "내 장소 타입 조회")
    @ApiResponse(responseCode = "200", description = "사용자 장소 타입 조회 성공")
    public ResponseEntity<UserPlaceTypeResponse> getUserPlaceTypes(
        @RequestAttribute("userId") long userId) {

        UserPlaceTypeResponse response = placeTypeService.getUserPlaceTypes(userId);

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/users/place-types")
    @Operation(summary = "사용자 장소 타입 수정", description = "장소 타입 수정, 1개 이상의 타입 선택 필요")
    @ApiResponse(responseCode = "200", description = "사용자 장소 타입 수정 성공")
    public ResponseEntity<UserPlaceTypeResponse> updateUserPlaceTypes(
        @RequestAttribute("userId") long userId,
        @RequestBody @Valid UserPlaceTypesRequest request) {

        UserPlaceTypeResponse response = placeTypeService.updateUserPlaceTypes(userId, request);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "전체 대화 장소 조회", description = "전체 대화 장소 조회")
    @ApiResponse(responseCode = "200", description = "전체 대화 장소 조회 성공")
    @GetMapping("/place-types")
    public ResponseEntity<PlaceTypeTotalResponse> getAllPlaceTypes() {

        PlaceTypeTotalResponse response = placeTypeService.getAllPlaceTypes();

        return ResponseEntity.ok().body(response);
    }
}
