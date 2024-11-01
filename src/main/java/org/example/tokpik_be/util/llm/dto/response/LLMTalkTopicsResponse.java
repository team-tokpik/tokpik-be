package org.example.tokpik_be.util.llm.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record LLMTalkTopicsResponse(
    @JsonProperty(required = true)
    List<LLMTalkTopicResponse> responses
) {

    public record LLMTalkTopicResponse(
        @JsonProperty(required = true)
        String title,

        @JsonProperty(required = true)
        String subTitle,

        @JsonProperty(required = true)
        String topicType,

        @JsonProperty(required = true)
        String placeType,

        @JsonProperty(required = true)
        String situation,

        @JsonProperty(required = true)
        Boolean talkPartnerGender,

        @JsonProperty(required = true)
        Integer talkPartnerAgeLowerBound,

        @JsonProperty(required = true)
        Integer talkPartnerAgeUpperBound
    ) {

    }
}
