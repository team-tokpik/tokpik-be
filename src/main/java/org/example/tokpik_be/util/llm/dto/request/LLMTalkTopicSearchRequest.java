package org.example.tokpik_be.util.llm.dto.request;

import java.util.List;
import lombok.Builder;
import org.example.tokpik_be.tag.domain.PlaceTag;
import org.example.tokpik_be.tag.domain.TopicTag;
import org.example.tokpik_be.talk_topic.dto.request.TalkTopicSearchRequest;
import org.springframework.util.CollectionUtils;

@Builder
public record LLMTalkTopicSearchRequest(
    List<String> topicTags,
    List<String> talkPurposes,
    List<String> talkPlaces,
    List<String> talkSituations,
    List<String> talkMoods,
    Boolean talkPartnerGender,
    Integer talkPartnerAgeLowerBound,
    Integer talkPartnerAgeUpperBound
) {

    public static LLMTalkTopicSearchRequest from(List<TopicTag> topicTags,
        List<PlaceTag> placeTags) {

        List<String> topicTagContents = topicTags.stream().map(TopicTag::getContent).toList();
        List<String> talkPlaces = placeTags.stream().map(PlaceTag::getContent).toList();

        return LLMTalkTopicSearchRequest.builder()
            .topicTags(topicTagContents)
            .talkPlaces(talkPlaces)
            .build();
    }

    public static LLMTalkTopicSearchRequest from(
        List<TopicTag> topicTags,
        List<PlaceTag> placeTags,
        TalkTopicSearchRequest request) {

        List<String> topicTagContents = topicTags.stream().map(TopicTag::getContent).toList();
        List<String> talkPlaces = placeTags.stream().map(PlaceTag::getContent).toList();

        return LLMTalkTopicSearchRequest.builder()
            .topicTags(topicTagContents)
            .talkPlaces(talkPlaces)
            .talkPurposes(request.talkPurposes())
            .talkSituations(request.talkSituations())
            .talkMoods(request.talkMoods())
            .talkPartnerGender(request.talkPartnerGender())
            .talkPartnerAgeLowerBound(request.talkPartnerAgeLowerBound())
            .talkPartnerAgeUpperBound(request.talkPartnerAgeUpperBound())
            .build();
    }

    public String toPromptContent() {

        String promptContent = """
            당신은 상황에 맞는 대화 주제를 추천해주는 전문가입니다. 다음 정보를 바탕으로 30개의 대화 주제를 추천해주세요.
            응답의 topicTag는 반드시 다음 옵션 중 하나로 설정해주세요 : %s
            응답의 placeTag는 반드시 주어진 대화 장소중에서 설정해주세요

            1. 대화 목적 : %s
            2. 대화 장소 : %s
            3. 대화 상황 : %s
            4. 대화 분위기 : %s,
            5. 대화 상대방 성별 : %s,
            6. 대화 상대방 연령대 : %s ~ %s세
                        
            다음 사항을 준수하며 대화 주제를 추천해주세요:
            - 각 주제는 주어진 상황, 맥락에 적절해야 합니다.
            - 대화 목적, 장소, 상황, 분위기에 여러 옵션이 속하는 경우, 각 옵션 중 하나 이상 부합하는 주제를 선정하세요.
                모든 옵션을 동시에 만족할 필요는 없습니다.
            - 상대방의 성별과 나이에 적합한 주제를 고르세요.
            - 대화 주제를 제시할 때 대화 주제 제목은 20자 내외, 대화 부제는 30자 내외로 제시해주세요.
            - 주어진 응답 형식을 엄수해주세요. 주어진 옵션과 일치하는 응답 값을 동일하게 설정해주세요.
            - null로 제시된 옵션을 무시해주세요.
            - 전형적이지 않고 재치 넘치는 주제를 선정해주세요.
            """;

        return promptContent.formatted(
            generatePromptConditions(this.topicTags),
            generatePromptConditions(this.talkPurposes),
            generatePromptConditions(this.talkPlaces),
            generatePromptConditions(this.talkSituations),
            generatePromptConditions(this.talkMoods),
            this.talkPartnerGender,
            this.talkPartnerAgeLowerBound,
            this.talkPartnerAgeUpperBound
        );
    }

    private String generatePromptConditions(List<String> conditions) {

        return CollectionUtils.isEmpty(conditions) ? null : String.join(", ", conditions);
    }
}
