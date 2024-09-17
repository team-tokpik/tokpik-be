package org.example.tokpik_be.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserProfileResponse(
    @Schema(type = "string", description = "마스킹 처리된 이메일", example = "m*****@naver.com")
    String maskedEmail,

    @Schema(type = "string", description = "프로필 사진 URL", example = "profile-photo/1-0")
    String profilePhotoUrl
) {

}
