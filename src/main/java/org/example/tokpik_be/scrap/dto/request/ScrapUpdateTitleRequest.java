package org.example.tokpik_be.scrap.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ScrapUpdateTitleRequest(
    @Schema(type = "string", description = "새 스크랩 제목", example = "여친과 하기 좋은 대화 주제")
    @NotBlank(message = "스크랩 제목은 필수값")
    String scrapTitle
) {

}
