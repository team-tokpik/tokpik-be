package org.example.tokpik_be.scrap.dto.response;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record ScrapResponse(
    @JsonProperty(required = true)
    @Schema(description = "스크랩에 포함된 대화 주제들")
    List<ScrapTopicResponse> contents,

    @JsonProperty(required = true)
    @Schema(type = "number", description = "제공된 데이터 중 가장 마지막 데이터 ID", example = "4")
    Long nextCursorId,

    @JsonProperty(required = true)
    @Schema(type = "boolean", description = "첫 페이지 여부", example = "true")
    boolean first,

    @JsonProperty(required = true)
    @Schema(type = "boolean", description = "마지막 페이지 여부", example = "false")
    boolean last

) {
    public record ScrapTopicResponse(
        @JsonProperty(required = true)
        @Schema(type = "number", description = "스크랩 대화 주제 ID", example = "1")
        Long scrapTopicId,
        @JsonProperty(required = true)
        @Schema(type = "number", description = "대화 주제 ID", example = "1")
        Long topicId,

        @JsonProperty(required = true)
        @Schema(type = "string", description = "대화 주제 제목", example = "대화 주제 제목")
        String topicTitle,

        @JsonProperty(required = true)
        @Schema(type = "string", description = "대화 주제 종류", example = "아이스브레이킹")
        String topicType,

        @JsonProperty(required = true)
        @Schema(type = "boolean", description = "대화 주제 스크랩 여부", example = "true")
        boolean scraped
    ) {
    }
}
