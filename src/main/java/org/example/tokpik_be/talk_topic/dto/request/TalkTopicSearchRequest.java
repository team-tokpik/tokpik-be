package org.example.tokpik_be.talk_topic.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record TalkTopicSearchRequest(
    @Schema(type = "boolean", description = "필터(검색 조건) 사용 여부", example = "true")
    boolean includeFilterCondition,

    @Schema(type = "array", description = "대화목적", example = "친목")
    List<@NotBlank(message = "대화목적은 유효한 문자열만 가능") String> talkPurposes,

    @Schema(type = "array", description = "대화상황", example = "첫만남")
    List<@NotBlank(message = "대화상황은 유효한 문자열만 가능") String> talkSituations,

    @Schema(type = "array", description = "대화분위기", example = "유익한 분위기")
    List<@NotBlank(message = "대화분위기는 유효한 문자열만 가능") String> talkMoods,

    @Schema(type = "boolean", description = "대화상대 성별, 남=true/여=false", example = "true")
    Boolean talkPartnerGender,

    @Schema(type = "number", description = "대화상대 연령대 하한", example = "20")
    Integer talkPartnerAgeLowerBound,

    @Schema(type = "number", description = "대화상대 연령대 상한", example = "25")
    Integer talkPartnerAgeUpperBound
) {

}
