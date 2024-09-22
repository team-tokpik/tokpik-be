package org.example.tokpik_be.talk_topic.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record TalkTopicSearchRequest(
    @Schema(type = "boolean", description = "필터(검색 조건) 사용 여부", example = "true")
    boolean includeFilterCondition,

    @Schema(type = "string", description = "대화목적", example = "친목")
    List<String> talkPurposes,

    @Schema(type = "string", description = "대화상황", example = "첫만남")
    List<String> talkSituations,

    @Schema(type = "string", description = "대화분위기", example = "유익한 분위기")
    List<String> talkMoods,

    @Schema(type = "boolean", description = "대화상대 성별, 남=true/여=false", example = "true")
    Boolean talkPartnerGender,

    @Schema(type = "number", description = "대화상대 연령대 하한", example = "20")
    Integer talkPartnerAgeLowerBound,

    @Schema(type = "number", description = "대화상대 연령대 상한", example = "25")
    Integer talkPartnerAgeUpperBound
) {

}
