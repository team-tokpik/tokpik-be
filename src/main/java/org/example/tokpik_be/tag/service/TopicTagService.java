package org.example.tokpik_be.tag.service;

import java.util.List;
import java.util.stream.Collectors;

import org.example.tokpik_be.tag.domain.TopicTag;
import org.example.tokpik_be.tag.dto.response.UserTopicTagResponse;
import org.example.tokpik_be.tag.entity.UserTopicTag;
import org.example.tokpik_be.tag.repository.UserTopicTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicTagService {

    @Autowired
    private UserTopicTagRepository userTopicTagRepository;

    public UserTopicTagResponse getUserTopicTags(long userId) {

        List<UserTopicTag> userTopicTags = userTopicTagRepository.findByUserId(userId);

        List<UserTopicTagResponse.TopicTagDTO> topicTagDTOList = userTopicTags.stream()
            .map(userTopicTag -> {
                TopicTag topicTag = userTopicTag.getTopicTag();
                return new UserTopicTagResponse.TopicTagDTO(
                    topicTag.getId(),
                    topicTag.getContent()
                );
            })
            .collect(Collectors.toList());

        return new UserTopicTagResponse(userId, topicTagDTOList);
    }
}
