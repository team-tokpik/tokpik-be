package org.example.tokpik_be.talk_topic.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TagException;
import org.example.tokpik_be.tag.domain.PlaceTag;
import org.example.tokpik_be.tag.domain.TopicTag;
import org.example.tokpik_be.tag.entity.UserPlaceTag;
import org.example.tokpik_be.tag.entity.UserTopicTag;
import org.example.tokpik_be.tag.repository.PlaceTagRepository;
import org.example.tokpik_be.tag.repository.TopicTagRepository;
import org.example.tokpik_be.talk_topic.domain.TalkPartner;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.talk_topic.dto.request.TalkTopicSearchRequest;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicsSearchResponse;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicsSearchResponse.TalkTopicSearchResponse;
import org.example.tokpik_be.talk_topic.repository.TalkTopicRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.enums.Gender;
import org.example.tokpik_be.user.service.UserQueryService;
import org.example.tokpik_be.util.llm.client.LLMApiClient;
import org.example.tokpik_be.util.llm.dto.request.LLMTalkTopicSearchRequest;
import org.example.tokpik_be.util.llm.dto.response.LLMTalkTopicsResponse;
import org.example.tokpik_be.util.llm.dto.response.LLMTalkTopicsResponse.LLMTalkTopicResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TalkTopicCommandService {

    private final TopicTagRepository topicTagRepository;
    private final PlaceTagRepository placeTagRepository;
    private final TalkTopicRepository talkTopicRepository;
    private final UserQueryService userQueryService;
    private final LLMApiClient llmApiClient;

    public TalkTopicsSearchResponse generateTopics(long userId, TalkTopicSearchRequest request) {
        List<TopicTag> topicTags = topicTagRepository.findAll();

        User user = userQueryService.findById(userId);
        LLMTalkTopicSearchRequest llmTalkTopicSearchRequest;

        if (request.includeFilterCondition()) {
            llmTalkTopicSearchRequest = LLMTalkTopicSearchRequest.from(topicTags, request);
        } else {
            List<TopicTag> userTopicTags = user.getUserTopicTags().stream()
                .map(UserTopicTag::getTopicTag).toList();
            List<PlaceTag> userPlaceTags = user.getUserPlaceTags().stream()
                .map(UserPlaceTag::getPlaceTag).toList();
            llmTalkTopicSearchRequest = LLMTalkTopicSearchRequest.from(userTopicTags,
                userPlaceTags);
        }

        LLMTalkTopicsResponse talkTopicsResponse = llmApiClient.searchTalkTopics(
            llmTalkTopicSearchRequest);
        List<PlaceTag> placeTags = placeTagRepository.findAll();

        List<TalkTopic> talkTopics = new ArrayList<>();
        for (LLMTalkTopicResponse response : talkTopicsResponse.responses()) {
            TopicTag topicTag = topicTags.stream()
                .filter(tag -> tag.getContent().equals(response.placeTag()))
                .findFirst()
                .orElseThrow(() -> new GeneralException(TagException.TAG_NOT_FOUND));

            PlaceTag placeTag = placeTags.stream()
                .filter(tag -> tag.getContent().equals(response.placeTag()))
                .findFirst()
                .orElseThrow(() -> new GeneralException(TagException.TAG_NOT_FOUND));

            Gender gender = Optional.ofNullable(response.talkPartnerGender())
                .map(Gender::from)
                .orElse(null);

            TalkPartner talkPartner = new TalkPartner(gender,
                response.talkPartnerAgeLowerBound(),
                response.talkPartnerAgeUpperBound());

            TalkTopic talkTopic = new TalkTopic(
                response.title(),
                response.subTitle(),
                response.situation(),
                talkPartner,
                topicTag,
                placeTag
            );
            talkTopics.add(talkTopic);
        }

        talkTopicRepository.saveAll(talkTopics);

        List<TalkTopicSearchResponse> responses = talkTopics.stream()
            .map(talkTopic -> TalkTopicSearchResponse.from(talkTopic, true))
            .toList();

        return new TalkTopicsSearchResponse(responses);
    }

}
