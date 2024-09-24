package org.example.tokpik_be.tag.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TagException;
import org.example.tokpik_be.tag.domain.PlaceTag;
import org.example.tokpik_be.tag.dto.request.UserPlaceTagsRequest;
import org.example.tokpik_be.tag.dto.response.UserPlaceTagResponse;
import org.example.tokpik_be.tag.entity.UserPlaceTag;
import org.example.tokpik_be.tag.repository.PlaceTagRepository;
import org.example.tokpik_be.tag.repository.UserPlaceTagRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceTagService {

    private final UserPlaceTagRepository userPlaceTagRepository;
    private final UserQueryService userQueryService;
    private final PlaceTagRepository placeTagRepository;

    public UserPlaceTagResponse getUserPlaceTags(long userId) {

        User user = userQueryService.findById(userId);

        List<UserPlaceTag> userPlaceTags = userPlaceTagRepository.findByUserId(user.getId());

        List<UserPlaceTagResponse.PlaceTagDTO> placeTagDTOList = userPlaceTags.stream()
            .map(userPlaceTag -> {
                PlaceTag placeTag = userPlaceTag.getPlaceTag();
                return new UserPlaceTagResponse.PlaceTagDTO(
                    placeTag.getId(),
                    placeTag.getContent()
                );
            })
            .toList();

        return new UserPlaceTagResponse(userId, placeTagDTOList);

    }

    @Transactional
    public UserPlaceTagResponse updateUserPlaceTags(long userId, UserPlaceTagsRequest request) {
        User user = userQueryService.findById(userId);

        if (request.placeTagIds() == null || request.placeTagIds().isEmpty()) {
            throw new GeneralException(TagException.INVALID_REQUEST);
        }

        userPlaceTagRepository.deleteByUserId(user.getId());

        for (long tagId : request.placeTagIds()) {
            if (!placeTagRepository.existsById(tagId)) {
                throw new GeneralException(TagException.TAG_NOT_FOUND);
            }
            UserPlaceTag userPlaceTag = new UserPlaceTag(user.getId(), placeTagRepository.findById(tagId).get());
            userPlaceTagRepository.save(userPlaceTag);
        }

        List<UserPlaceTag> updatedTags = userPlaceTagRepository.findByUserId(user.getId());
        List<UserPlaceTagResponse.PlaceTagDTO> placeTagDTOList = updatedTags.stream()
            .map(tag -> new UserPlaceTagResponse.PlaceTagDTO(tag.getPlaceTag().getId(), tag.getPlaceTag().getContent()))
            .toList();

        return new UserPlaceTagResponse(userId, placeTagDTOList);
    }
}
