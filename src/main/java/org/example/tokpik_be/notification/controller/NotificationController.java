package org.example.tokpik_be.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.notification.dto.request.NotificationCreateRequest;
import org.example.tokpik_be.notification.dto.response.NotificationDetailResponse;
import org.example.tokpik_be.notification.dto.response.NotificationsResponse;
import org.example.tokpik_be.notification.service.NotificationCommandService;
import org.example.tokpik_be.notification.service.NotificationQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "알림 API", description = "알림 연관 API")
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;

    @Operation(summary = "알림 삭제", description = "알림 삭제")
    @ApiResponse(responseCode = "200", description = "알림 삭제 성공")
    @DeleteMapping("/users/notifications/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@RequestAttribute("userId") long userId,
        @PathVariable("notificationId") long notificationId) {

        notificationCommandService.deleteNotification(userId, notificationId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "알림 목록 조회", description = "알림 목록 조회, 한 페이지에 10개 알림 제공")
    @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공")
    @GetMapping("/users/notifications")
    public ResponseEntity<NotificationsResponse> getNotifications(
        @RequestAttribute("userId") long userId,
        @Parameter(name = "nextCursorId", description = "마지막 알림 ID, 커서 페이징에 사용",
            in = ParameterIn.QUERY)
        @RequestParam(name = "nextCursorId", required = false) Long nextCursorId) {

        NotificationsResponse response = notificationQueryService
            .getNotifications(userId, nextCursorId);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "알림 생성", description = "알림 생성")
    @ApiResponse(responseCode = "200", description = "알림 생성 성공")
    @PostMapping("/users/notifications")
    public ResponseEntity<Void> createNotification(@RequestAttribute("userId") long userId,
        @RequestBody @Valid NotificationCreateRequest request) {

        notificationCommandService.createNotification(userId, request);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "알림 상세 조회", description = "알림 상세 내역 조회")
    @ApiResponse(responseCode = "200", description = "알림 상세 조회 성공")
    @GetMapping("/users/notifications/{notificationId}/details")
    public ResponseEntity<NotificationDetailResponse> getNotificationDetail(
        @RequestAttribute("userId") long userId,
        @PathVariable("notificationId") long notificationId) {

        NotificationDetailResponse response = notificationQueryService
            .getNotificationDetail(userId, notificationId);

        return ResponseEntity.ok().body(response);
    }
}
