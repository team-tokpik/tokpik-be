package org.example.tokpik_be.tag.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.example.tokpik_be.tag.domain.TopicTag;

public record TopicTagTotalResponse(
    @Schema(type = "array", description = "대화 종류들")
    List<TopicTagResponse> topicTags
) {

    public record TopicTagResponse(
        @Schema(type = "number", description = "대화 종류 ID", example = "1")
        long topicTagId,

        @Schema(type = "string", description = "대화 종류 내용", example = "비즈니스와 업무")
        String content
    ) {

        public static TopicTagResponse from(TopicTag topicTag) {

            return new TopicTagResponse(topicTag.getId(), topicTag.getContent());
        }
    }
}
