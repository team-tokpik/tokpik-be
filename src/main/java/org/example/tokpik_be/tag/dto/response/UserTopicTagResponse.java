package org.example.tokpik_be.tag.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserTopicTagResponse", description = "사용자 대화 태그 응답")
public record UserTopicTagResponse(
    @Schema(type="number", description = "사용자 ID", example = "1")
    Long userId,

    @Schema(description = "대화 태그 목록")
    List<TopicTagDTO> talkTopicTags
) {

    @Schema(name = "TopicTagDTO", description = "대화 태그 정보")
    public record TopicTagDTO(
        @Schema(type="number", description = "대화 태그 ID", example = "1")
        Long id,

        @Schema(type = "string", description = "대화 태그 내용", example = "자기계발")
        String content
    ) {

    }
}
