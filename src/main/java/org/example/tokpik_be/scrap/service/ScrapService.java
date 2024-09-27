package org.example.tokpik_be.scrap.service;

import java.util.List;

import org.example.tokpik_be.scrap.domain.Scrap;
import org.example.tokpik_be.scrap.domain.ScrapTopic;
import org.example.tokpik_be.scrap.dto.response.ScrapCountResponse;
import org.example.tokpik_be.scrap.dto.response.ScrapListResponse;
import org.example.tokpik_be.scrap.repository.ScrapRepository;
import org.example.tokpik_be.scrap.repository.ScrapTopicRepository;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final ScrapTopicRepository scrapTopicRepository;
    private final UserQueryService userQueryService;

    public ScrapListResponse getScrapList(long userId) {

        User user = userQueryService.findById(userId);

        List<Scrap> scraps = scrapRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        List<ScrapListResponse.ScrapResponse> scrapResponses = scraps.stream()
            .map(this::mapToScrapResponse)
            .toList();

        return new ScrapListResponse(scrapResponses);
    }

    private ScrapListResponse.ScrapResponse mapToScrapResponse(Scrap scrap) {
        List<ScrapTopic> scrapTopics = scrapTopicRepository.findByScrapOrderByCreatedAtDesc(scrap);

        List<ScrapListResponse.TopicTypeResponse> topicTypes = scrapTopics.stream()
            .map(this::mapToTopicTypeResponse)
            .toList();

        return new ScrapListResponse.ScrapResponse(
            scrap.getId(),
            scrap.getTitle(),
            topicTypes
        );
    }

    private ScrapListResponse.TopicTypeResponse mapToTopicTypeResponse(ScrapTopic scrapTopic) {
        TalkTopic talkTopic = scrapTopic.getTalkTopic();
        return new ScrapListResponse.TopicTypeResponse(
            talkTopic.getTopicTag().getId(),
            talkTopic.getTopicTag().getContent()
        );
    }

    public ScrapCountResponse getUserSrcapCounts(long userId){

        User user = userQueryService.findById(userId);

        Long count = scrapRepository.countByUser(user);

        return new ScrapCountResponse(count);
    }

    public ScrapCountResponse getUserTopicCounts(long userId){

        User user = userQueryService.findById(userId);

        Long count = scrapTopicRepository.countByUserId(userId);

        return new ScrapCountResponse(count);
    }
}
