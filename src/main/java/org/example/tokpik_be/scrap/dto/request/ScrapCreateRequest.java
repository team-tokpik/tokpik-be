package org.example.tokpik_be.scrap.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ScrapCreateRequest(
    @Schema(type = "string", description = "스크랩 이름(제목)", example = "여친과 하기 좋은 대화 주제")
    @NotBlank(message = "스크랩 이름은 필수값")
    String scrapName
) {

}
