package org.example.tokpik_be.tag.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TagException;
import org.example.tokpik_be.tag.domain.TopicTag;
import org.example.tokpik_be.tag.dto.request.UserTopicTagsRequest;
import org.example.tokpik_be.tag.dto.response.TopicTagTotalResponse;
import org.example.tokpik_be.tag.dto.response.TopicTagTotalResponse.TopicTagResponse;
import org.example.tokpik_be.tag.dto.response.UserTopicTagResponse;
import org.example.tokpik_be.tag.domain.UserTopicTag;
import org.example.tokpik_be.tag.repository.TopicTagRepository;
import org.example.tokpik_be.tag.repository.UserTopicTagRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TopicTagService {

    private final UserTopicTagRepository userTopicTagRepository;
    private final UserQueryService userQueryService;
    private final TopicTagRepository topicTagRepository;

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
            .toList();

        return new UserTopicTagResponse(userId, topicTagDTOList);
    }

    @Transactional
    public UserTopicTagResponse updateUserTopicTags(long userId, UserTopicTagsRequest request) {
        User user = userQueryService.findById(userId);

        if (request.topicTagIds() == null || request.topicTagIds().isEmpty()) {
            throw new GeneralException(TagException.INVALID_REQUEST);
        }

        userTopicTagRepository.deleteByUserId(user.getId());

        for (long tagId : request.topicTagIds()) {
            if (!topicTagRepository.existsById(tagId)) {
                throw new GeneralException(TagException.TAG_NOT_FOUND);
            }
            UserTopicTag userTopicTag = new UserTopicTag(user.getId(),
                topicTagRepository.findById(tagId).get());
            userTopicTagRepository.save(userTopicTag);
        }

        List<UserTopicTag> updatedTags = userTopicTagRepository.findByUserId(user.getId());
        List<UserTopicTagResponse.TopicTagDTO> topicTagDTOList = updatedTags.stream()
            .map(tag -> new UserTopicTagResponse.TopicTagDTO(tag.getTopicTag().getId(),
                tag.getTopicTag().getContent()))
            .toList();

        return new UserTopicTagResponse(userId, topicTagDTOList);
    }

    @Transactional(readOnly = true)
    public TopicTagTotalResponse getAllTopicTags() {
        List<TopicTag> topicTags = topicTagRepository.findAll();
        List<TopicTagResponse> responses = topicTags.stream().map(TopicTagResponse::from).toList();

        return new TopicTagTotalResponse(responses);
    }
}
