package org.example.tokpik_be.type.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import org.example.tokpik_be.scrap.dto.response.ScrapListResponse;
import org.example.tokpik_be.type.domain.TopicType;

public record TopicTypeTotalResponse(
    @Schema(type = "array", description = "대화 종류들")
    List<TopicTypeResponse> topicTypes
) {

    public record TopicTypeResponse(
        @Schema(type = "number", description = "대화 종류 ID", example = "1")
        long topicTypeId,

        @Schema(type = "string", description = "대화 종류 내용", example = "비즈니스와 업무")
        String content
    ) {

        public static TopicTypeResponse from(TopicType topicType) {

            return new TopicTypeResponse(topicType.getId(), topicType.getContent());
        }
    }
}
