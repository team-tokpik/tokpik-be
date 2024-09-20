package org.example.tokpik_be.tag.service;

import java.util.List;
import java.util.stream.Collectors;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.UserException;
import org.example.tokpik_be.tag.domain.TopicTag;
import org.example.tokpik_be.tag.dto.response.UserTopicTagResponse;
import org.example.tokpik_be.tag.entity.UserTopicTag;
import org.example.tokpik_be.tag.repository.UserTopicTagRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.repository.UserRepository;
import org.example.tokpik_be.user.service.UserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicTagService {

    private final UserTopicTagRepository userTopicTagRepository;
    private final UserQueryService userQueryService;

    private final UserRepository userRepository;

    public UserTopicTagResponse getUserTopicTags(long userId) {

        User user = userQueryService.findById(userId);

        List<UserTopicTag> userTopicTags = userTopicTagRepository.findByUserId(user.getId());

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
