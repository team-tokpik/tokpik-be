package org.example.tokpik_be.scrap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ScrapCreateResponse(
    @Schema(type = "number", description = "생성된 스크랩 ID", example = "1")
    long scrapId
) {

}
