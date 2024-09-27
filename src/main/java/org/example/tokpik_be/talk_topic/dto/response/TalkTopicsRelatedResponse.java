package org.example.tokpik_be.talk_topic.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record TalkTopicsRelatedResponse(
    @Schema(type = "array", description = "연관 대화 주제들")
    List<TalkTopicRelatedResponse> talkTopics
) {

    public record TalkTopicRelatedResponse(
        @Schema(type = "number", description = "대화 주제 ID", example = "1")
        long topicId,

        @Schema(type = "string", description = "대화 주제 종류", example = "인관관계")
        String type,

        @Schema(type = "string", description = "대화 주제 제목", example = "쓰@껄하게 스몰톡하는 법")
        String title,

        @Schema(type = "boolean", description = "스크랩 여부", example = "false")
        boolean scraped
    ) {

    }
}
