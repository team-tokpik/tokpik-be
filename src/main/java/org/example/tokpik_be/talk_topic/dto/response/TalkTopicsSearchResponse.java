package org.example.tokpik_be.talk_topic.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;

public record TalkTopicsSearchResponse(
    @Schema(type = "array", description = "대화 주제들")
    List<TalkTopicSearchResponse> topics
) {

    public record TalkTopicSearchResponse(
        @Schema(type = "number", description = "대화 주제 ID", example = "1")
        long topicId,

        @Schema(type = "string", description = "제목", example = "MBTI/T발 너 C야?")
        String title,

        @Schema(type = "string", description = "부제목", example = "MBTI, 확실히 알려드릴게요")
        String subtitle,

        @Schema(type = "string", description = "대화태그", example = "요즘이슈")
        String topicTag,

        @Schema(type = "string", description = "장소태그", example = "학교")
        String placeTag,

        @Schema(type = "boolean", description = "스크랩 여부", example = "true")
        boolean scraped) {

        public static TalkTopicSearchResponse from(TalkTopic talkTopic, boolean scraped) {

            return new TalkTopicSearchResponse(talkTopic.getId(),
                talkTopic.getTitle(),
                talkTopic.getSubtitle(),
                talkTopic.getTopicTag().getContent(),
                talkTopic.getPlaceTag().getContent(),
                scraped
            );
        }
    }
}
