package org.example.tokpik_be.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.tokpik_be.user.domain.User;

public record UserProfileResponse(
    @Schema(type = "string", description = "마스킹 처리된 이메일", example = "mj****@naver.com")
    String maskedEmail,

    @Schema(type = "string", description = "프로필 사진 URL", example = "profile-photo/1-0")
    String profilePhotoUrl
) {

    public static UserProfileResponse from(User user) {

        return new UserProfileResponse(user.getEmail(), user.getProfilePhotoUrl());
    }
}
