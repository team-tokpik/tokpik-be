package org.example.tokpik_be.type.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserTopicTypeResponse", description = "사용자 대화 태그 응답")
public record UserTopicTypeResponse(
    @Schema(type="number", description = "사용자 ID", example = "1")
    Long userId,

    @Schema(type = "array", description = "대화 태그 목록")
    List<TopicTypeDTO> talkTopicTypes
) {

    @Schema(name = "TopicTypeDTO", description = "대화 태그 정보")
    public record TopicTypeDTO(
        @Schema(type="number", description = "대화 태그 ID", example = "1")
        Long id,

        @Schema(type = "string", description = "대화 태그 내용", example = "자기계발")
        String content
    ) {

    }
}
