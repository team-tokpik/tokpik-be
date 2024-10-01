package org.example.tokpik_be.policy.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PolicyResponse(
    @Schema(type = "string", description = "개인정보정책 제목", example = "데이터 분석")
    String title,

    @Schema(type = "string", description = "개인정보정책 내용", example = "일부 모바일 앱에서 사용자의..")
    String content
){
}
