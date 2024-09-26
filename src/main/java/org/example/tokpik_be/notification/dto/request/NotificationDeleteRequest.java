package org.example.tokpik_be.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record NotificationDeleteRequest(
    @Schema(type = "number", description = "알림 ID", example = "1")
    long notificationId,

    @Schema(type = "number", description = "알림 간격(분)", example = "10")
    int notificationIntervalMinutes
) {

}
