package org.example.tokpik_be.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateNotificationTokenRequest(
    @Schema(type = "string", description = "notification token", example = "header.payload.signature")
    @NotBlank(message = "notification token은 필수값")
    String notificationToken
) {

}
