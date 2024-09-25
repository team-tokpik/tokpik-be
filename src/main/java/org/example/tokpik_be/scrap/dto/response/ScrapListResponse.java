package org.example.tokpik_be.scrap.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record ScrapListResponse(
    @JsonProperty(required = true)
    @Schema(description = "스크랩 목록")
    List<ScrapResponse> scraps
) {
    public record ScrapResponse(
        @JsonProperty(required = true)
        @Schema(type = "number", description = "스크랩 ID", example = "1")
        Long scrapId,

        @JsonProperty(required = true)
        @Schema(type = "string", description = "스크랩 이름", example = "친구과 스몰톡하기 좋은 주제들")
        String scrapName,

        @JsonProperty(required = true)
        @Schema(description = "최근 주제 유형 목록")
        List<TopicTypeResponse> recentTopicTypes
    ) {

    }

    public record TopicTypeResponse(
        @JsonProperty(required = true)
        @Schema(type = "number", description = "주제 유형 ID", example = "3")
        Long topicTypeId,

        @JsonProperty(required = true)
        @Schema(type = "string", description = "주제 유형 내용", example = "아이스브레이킹")
        String topicTypeContent
    ) {

    }
}
