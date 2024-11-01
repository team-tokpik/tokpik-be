package org.example.tokpik_be.type.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TypeException;
import org.example.tokpik_be.type.domain.PlaceType;
import org.example.tokpik_be.type.dto.request.UserPlaceTypesRequest;
import org.example.tokpik_be.type.dto.response.PlaceTypeTotalResponse;
import org.example.tokpik_be.type.dto.response.PlaceTypeTotalResponse.PlaceTypeResponse;
import org.example.tokpik_be.type.dto.response.UserPlaceTypeResponse;
import org.example.tokpik_be.type.domain.UserPlaceType;
import org.example.tokpik_be.type.repository.PlaceTypeRepository;
import org.example.tokpik_be.type.repository.UserPlaceTypeRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceTypeService {

    private final UserPlaceTypeRepository userPlaceTypeRepository;
    private final UserQueryService userQueryService;
    private final PlaceTypeRepository placeTypeRepository;

    public UserPlaceTypeResponse getUserPlaceTypes(long userId) {

        User user = userQueryService.findById(userId);

        List<UserPlaceType> userPlaceTypes = userPlaceTypeRepository.findByUserId(user.getId());

        List<UserPlaceTypeResponse.PlaceTypeDTO> placeTypeDTOList = userPlaceTypes.stream()
            .map(userPlaceType -> {
                PlaceType placeType = userPlaceType.getPlaceType();
                return new UserPlaceTypeResponse.PlaceTypeDTO(
                    placeType.getId(),
                    placeType.getContent()
                );
            })
            .toList();

        return new UserPlaceTypeResponse(userId, placeTypeDTOList);

    }

    @Transactional
    public UserPlaceTypeResponse updateUserPlaceTypes(long userId, UserPlaceTypesRequest request) {
        User user = userQueryService.findById(userId);

        if (request.placeTypeIds() == null || request.placeTypeIds().isEmpty()) {
            throw new GeneralException(TypeException.INVALID_REQUEST);
        }

        if(request.placeTypeIds().size() != request.placeTypeIds().stream().distinct().count()) {
            throw new GeneralException(TypeException.DUPLICATE_TYPES);
        }

        userPlaceTypeRepository.deleteByUserId(user.getId());

        for (long typeId : request.placeTypeIds()) {
            if (!placeTypeRepository.existsById(typeId)) {
                throw new GeneralException(TypeException.TYPE_NOT_FOUND);
            }
            UserPlaceType userPlaceType = new UserPlaceType(user.getId(),
                placeTypeRepository.findById(typeId).get());
            userPlaceTypeRepository.save(userPlaceType);
        }

        List<UserPlaceType> updatedTypes = userPlaceTypeRepository.findByUserId(user.getId());
        List<UserPlaceTypeResponse.PlaceTypeDTO> placeTypeDTOList = updatedTypes.stream()
            .map(Type -> new UserPlaceTypeResponse.PlaceTypeDTO(Type.getPlaceType().getId(),
                Type.getPlaceType().getContent()))
            .toList();

        return new UserPlaceTypeResponse(userId, placeTypeDTOList);
    }

    @Transactional(readOnly = true)
    public PlaceTypeTotalResponse getAllPlaceTypes() {
        List<PlaceType> placeTypes = placeTypeRepository.findAll();
        List<PlaceTypeResponse> responses = placeTypes.stream().map(PlaceTypeResponse::from).toList();

        return new PlaceTypeTotalResponse(responses);
    }
}
