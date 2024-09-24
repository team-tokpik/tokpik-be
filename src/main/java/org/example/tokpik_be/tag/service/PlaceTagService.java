package org.example.tokpik_be.tag.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.tag.domain.PlaceTag;
import org.example.tokpik_be.tag.dto.response.UserPlaceTagResponse;
import org.example.tokpik_be.tag.entity.UserPlaceTag;
import org.example.tokpik_be.tag.repository.PlaceTagRepository;
import org.example.tokpik_be.tag.repository.UserPlaceTagRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.springframework.stereotype.Service;

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
}
