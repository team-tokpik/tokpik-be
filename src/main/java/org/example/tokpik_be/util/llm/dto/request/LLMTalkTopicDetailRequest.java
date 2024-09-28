package org.example.tokpik_be.util.llm.dto.request;

import org.example.tokpik_be.talk_topic.domain.TalkPartner;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;

public record LLMTalkTopicDetailRequest(
    String title,
    String subTitle,
    String situation,
    TalkPartner talkPartner,
    String topicTag,
    String placeTag
) {

    public static LLMTalkTopicDetailRequest from(TalkTopic talkTopic) {

        return new LLMTalkTopicDetailRequest(talkTopic.getTitle(),
            talkTopic.getSubtitle(),
            talkTopic.getSituation(),
            talkTopic.getPartner(),
            talkTopic.getTopicTag().getContent(),
            talkTopic.getPlaceTag().getContent());
    }

    public String toPromptContent() {
        String promptContent = """
            당신은 주어진 키워드들에 맞는 상세 대화 내용을 제시하는 전문가입니다.
            다음 정보를 바탕으로 상세 내용을 항목 제목, 항목 내용 구성에 따라 제안해주세요.
            응답의 itemTitle는 상세 내용 항목 제목으로 소제목 형태를 띄게 됩니다.
            응답의 itemContent는 상세 내용 항목 내용으로  소제목별 세부 내용을 의미합니다.
                        
            1. 대화 주제 제목 : %s
            2. 대화 주제 부제목 : %s
            3. 대화 주제 분위기 : %s
            4. 대화 상대 성별 : %s
            5. 대화 상대 연령대 : %s ~ %s세
            6. 대화 주제 분류 : %s
            7. 대화 장소 : %s
                        
            다음 사항을 준수하여 상세 내용을 생성해주세요.
            - 상세 내용은 주어진 키워드들에 적절해야 합니다.
            - 상세 내용 항목은 5개 이내로 구성해주세요.
            - 항목별 소제목은 30자 내외, 세부 내용은 100자 이상으로 구성해주세요.
            - 전형적이지 않고 재치 넘치게 내용을 생성해주세요.
            """;

        return promptContent.formatted(
            title,
            subTitle,
            situation,
            talkPartner.getGender().getDescription(),
            talkPartner.getAgeLowerBound(), talkPartner.getAgeUpperBound(),
            topicTag,
            placeTag);
    }
}
