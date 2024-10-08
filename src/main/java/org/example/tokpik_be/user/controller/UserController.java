package org.example.tokpik_be.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.user.dto.request.UserMakeProfileRequest;
import org.example.tokpik_be.user.dto.request.UserUpdateNotificationTokenRequest;
import org.example.tokpik_be.user.dto.response.UserProfileResponse;
import org.example.tokpik_be.user.service.UserCommandService;
import org.example.tokpik_be.user.service.UserQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 API", description = "사용자 연관 API")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @Operation(summary = "프로필(부가 정보) 생성", description = "사용자 부가 정보 입력")
    @ApiResponse(responseCode = "200", description = "프로필 생성 성공")
    @PostMapping("/users/profiles")
    public ResponseEntity<Void> makeProfile(@RequestAttribute("userId") long userId,
        @RequestBody @Valid UserMakeProfileRequest request) {

        userCommandService.makeProfile(userId, request);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "마이 페이지 프로필 조회", description = "마이 페이지 사용자 프로필 조회")
    @ApiResponse(responseCode = "200", description = "프로필 조회 성공")
    @GetMapping("/users/profiles")
    public ResponseEntity<UserProfileResponse> getUserProfile(
        @RequestAttribute("userId") long userId) {

        UserProfileResponse response = userQueryService.getUserProfile(userId);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "notification token 갱신", description = "notification token 갱신")
    @ApiResponse(responseCode = "200", description = "notification token 갱신 성공")
    @PutMapping("/users/notification-token")
    public ResponseEntity<Void> updateNotificationToken(
        @RequestAttribute("userId") long userId,
        @RequestBody @Valid UserUpdateNotificationTokenRequest request) {

        userCommandService.updateNotificationToken(userId, request);
        
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴")
    @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공")
    @DeleteMapping("/users")
    public ResponseEntity<Void> deleteUser(
        @RequestAttribute("userId") long userId) {

        userCommandService.deleteUser(userId);

        return ResponseEntity.ok().build();
    }
}
