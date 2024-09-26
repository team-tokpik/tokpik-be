package org.example.tokpik_be.talk_topic.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
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
        List<PlaceTag> placeTags = placeTagRepository.findAll();
        User user = userQueryService.findById(userId);

        LLMTalkTopicSearchRequest llmRequest = createLLMRequest(request, user, topicTags,
            placeTags);
        LLMTalkTopicsResponse llmResponse = llmApiClient.searchTalkTopics(llmRequest);

        List<TalkTopic> talkTopics = createTalkTopics(llmResponse, topicTags, placeTags);
        talkTopicRepository.saveAll(talkTopics);

        return new TalkTopicsSearchResponse(talkTopics.stream()
            .map(talkTopic -> TalkTopicSearchResponse.from(talkTopic, false))
            .toList());
    }

    private LLMTalkTopicSearchRequest createLLMRequest(TalkTopicSearchRequest request,
        User user,
        List<TopicTag> topicTags,
        List<PlaceTag> placeTags) {
        if (request.includeFilterCondition()) {

            return LLMTalkTopicSearchRequest.from(topicTags, placeTags, request);
        }

        List<TopicTag> userTopicTags = user.getUserTopicTags().stream()
            .map(UserTopicTag::getTopicTag)
            .toList();
        List<PlaceTag> userPlaceTags = user.getUserPlaceTags().stream()
            .map(UserPlaceTag::getPlaceTag)
            .toList();

        return LLMTalkTopicSearchRequest.from(userTopicTags, userPlaceTags);
    }

    private List<TalkTopic> createTalkTopics(LLMTalkTopicsResponse llmResponse,
        List<TopicTag> topicTags,
        List<PlaceTag> placeTags) {
        Map<String, TopicTag> topicTagMap = createTagMap(topicTags, TopicTag::getContent);
        Map<String, PlaceTag> placeTagMap = createTagMap(placeTags, PlaceTag::getContent);

        return llmResponse.responses().stream()
            .map(response -> createTalkTopic(response, topicTagMap, placeTagMap))
            .toList();
    }

    private <T> Map<String, T> createTagMap(List<T> tags, Function<T, String> keyExtractor) {

        return tags.stream().collect(Collectors.toMap(keyExtractor, Function.identity()));
    }

    private TalkTopic createTalkTopic(LLMTalkTopicResponse response,
        Map<String, TopicTag> topicTagMap,
        Map<String, PlaceTag> placeTagMap) {
        TopicTag topicTag = Optional.ofNullable(topicTagMap.get(response.topicTag()))
            .orElseThrow(() -> new GeneralException(TagException.TAG_NOT_FOUND));
        PlaceTag placeTag = Optional.ofNullable(placeTagMap.get(response.placeTag()))
            .orElseThrow(() -> new GeneralException(TagException.TAG_NOT_FOUND));

        TalkPartner talkPartner = new TalkPartner(
            Gender.from(response.talkPartnerGender()),
            response.talkPartnerAgeLowerBound(),
            response.talkPartnerAgeUpperBound()
        );

        return new TalkTopic(response.title(),
            response.subTitle(),
            response.situation(),
            talkPartner,
            topicTag,
            placeTag);
    }
}
