package org.example.tokpik_be.type.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TypeException;
import org.example.tokpik_be.type.domain.TopicType;
import org.example.tokpik_be.type.domain.UserTopicType;
import org.example.tokpik_be.type.dto.request.UserTopicTypesRequest;
import org.example.tokpik_be.type.dto.response.TopicTypeTotalResponse;
import org.example.tokpik_be.type.dto.response.TopicTypeTotalResponse.TopicTypeResponse;
import org.example.tokpik_be.type.dto.response.UserTopicTypeResponse;
import org.example.tokpik_be.type.repository.TopicTypeRepository;
import org.example.tokpik_be.type.repository.UserTopicTypeRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TopicTypeService {

    private final UserTopicTypeRepository userTopicTypeRepository;
    private final UserQueryService userQueryService;
    private final TopicTypeRepository topicTypeRepository;

    public UserTopicTypeResponse getUserTopicTypes(long userId) {

        User user = userQueryService.findById(userId);

        List<UserTopicType> userTopicTypes = userTopicTypeRepository.findByUserId(user.getId());

        List<UserTopicTypeResponse.TopicTypeDTO> topicTypeDTOList = userTopicTypes.stream()
            .map(userTopicType -> {
                TopicType topicType = userTopicType.getTopicType();
                return new UserTopicTypeResponse.TopicTypeDTO(
                    topicType.getId(),
                    topicType.getContent()
                );
            })
            .toList();

        return new UserTopicTypeResponse(userId, topicTypeDTOList);
    }

    @Transactional
    public UserTopicTypeResponse updateUserTopicTypes(long userId, UserTopicTypesRequest request) {
        User user = userQueryService.findById(userId);

        if (request.topicTypeIds() == null || request.topicTypeIds().isEmpty()) {
            throw new GeneralException(TypeException.INVALID_REQUEST);
        }

        if(request.topicTypeIds().size() != request.topicTypeIds().stream().distinct().count()) {
            throw new GeneralException(TypeException.DUPLICATE_TYPES);
        }

        userTopicTypeRepository.deleteByUserId(user.getId());

        for (long typeId : request.topicTypeIds()) {
            if (!topicTypeRepository.existsById(typeId)) {
                throw new GeneralException(TypeException.TYPE_NOT_FOUND);
            }
            UserTopicType userTopicType = new UserTopicType(user.getId(),
                topicTypeRepository.findById(typeId).get());
            userTopicTypeRepository.save(userTopicType);
        }

        List<UserTopicType> updatedTypes = userTopicTypeRepository.findByUserId(user.getId());
        List<UserTopicTypeResponse.TopicTypeDTO> topicTypeDTOList = updatedTypes.stream()
            .map(type -> new UserTopicTypeResponse.TopicTypeDTO(type.getTopicType().getId(),
                type.getTopicType().getContent()))
            .toList();

        return new UserTopicTypeResponse(userId, topicTypeDTOList);
    }

    @Transactional(readOnly = true)
    public TopicTypeTotalResponse getAllTopicTypes() {
        List<TopicType> topicTypes = topicTypeRepository.findAll();
        List<TopicTypeResponse> responses = topicTypes.stream().map(TopicTypeResponse::from).toList();

        return new TopicTypeTotalResponse(responses);
    }
}
