package org.example.tokpik_be.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.user.dto.response.UserProfileResponse;
import org.example.tokpik_be.user.service.UserQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserQueryService userQueryService;

    @Operation(summary = "마이 페이지 프로필 조회", description = "마이 페이지 사용자 프로필 조회")
    @ApiResponse(responseCode = "200", description = "프로필 조회 성공")
    @GetMapping("/users/profiles")
    public ResponseEntity<UserProfileResponse> getUserProfile(
        @RequestAttribute("userId") long userId) {

        UserProfileResponse response = userQueryService.getUserProfile(userId);

        return ResponseEntity.ok().body(response);
    }
}
