package org.example.tokpik_be.talk_topic.service;

import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TalkTopicException;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;
import org.example.tokpik_be.talk_topic.repository.TalkTopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TalkTopicQueryService {

    private final TalkTopicRepository talkTopicRepository;

    public TalkTopic findById(long topicId) {

        return talkTopicRepository.findById(topicId)
            .orElseThrow(() -> new GeneralException(TalkTopicException.TALK_TOPIC_NOT_FOUND));
    }
}
