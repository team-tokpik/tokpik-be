package org.example.tokpik_be.type.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.List;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TypeException;
import org.example.tokpik_be.type.domain.PlaceType;
import org.example.tokpik_be.type.domain.UserPlaceType;
import org.example.tokpik_be.type.dto.request.UserPlaceTypesRequest;
import org.example.tokpik_be.type.dto.response.PlaceTypeTotalResponse;
import org.example.tokpik_be.type.dto.response.UserPlaceTypeResponse;
import org.example.tokpik_be.type.repository.PlaceTypeRepository;
import org.example.tokpik_be.type.repository.UserPlaceTypeRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PlaceTypeServiceTest {
    @Mock
    private PlaceTypeRepository placeTypeRepository;

    @Mock
    private UserPlaceTypeRepository userPlaceTypeRepository;

    @Mock
    private UserQueryService userQueryService;

    @InjectMocks
    private PlaceTypeService placeTypeService;

    private User mockUser(long userId) {
        return new User("ex@example.com", "https://www.example.com/profile-photo") {
            @Override
            public Long getId() {
                return userId;
            }
        };
    }

    @Test
    @DisplayName("사용자의 장소 타입을 조회할 수 있다.")
    void getMyPlaceTypes() {
        // given
        long userId = 1L;
        User user = mockUser(userId);
        when(userQueryService.findById(userId)).thenReturn(user);

        PlaceType placeType1 = new PlaceType("집");
        PlaceType placeType2 = new PlaceType("학교");

        UserPlaceType userPlaceType1 = new UserPlaceType(userId, placeType1);
        UserPlaceType userPlaceType2 = new UserPlaceType(userId, placeType2);

        List<UserPlaceType> userPlaceTypes = List.of(userPlaceType1, userPlaceType2);

        when(userPlaceTypeRepository.findByUserId(anyLong())).thenReturn(userPlaceTypes);

        // when
        UserPlaceTypeResponse response = placeTypeService.getUserPlaceTypes(userId);

        // then
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.placeTopicTypes())
            .hasSize(2)
            .map(UserPlaceTypeResponse.PlaceTypeDTO::content)
            .containsExactlyInAnyOrder("집", "학교");
    }

    @Nested
    @DisplayName("사용자 장소 타입 수정 시 ")
    class UpdatePlaceTypesTest {
        @Test
        @DisplayName("성공한다.")
        void updateMyPlaceTypes() {
            // given
            long userId = 1L;
            User user = mockUser(userId);
            when(userQueryService.findById(userId)).thenReturn(user);

            PlaceType placeType1 = new PlaceType(1L, "집");
            PlaceType placeType3 = new PlaceType(3L, "직장");

            when(placeTypeRepository.findAllById(List.of(1L, 3L)))
                .thenReturn(List.of(placeType1, placeType3));

            List<Long> newTypes = List.of(1L, 3L);
            UserPlaceTypesRequest request = new UserPlaceTypesRequest(newTypes);

            UserPlaceType userPlaceType1 = new UserPlaceType(userId, placeType1);
            UserPlaceType userPlaceType3 = new UserPlaceType(userId, placeType3);
            when(userPlaceTypeRepository.findByUserId(userId))
                .thenReturn(List.of(userPlaceType1, userPlaceType3));

            // when
            UserPlaceTypeResponse response = placeTypeService.updateUserPlaceTypes(userId, request);

            // then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.placeTopicTypes())
                .hasSize(2)
                .allSatisfy(types -> {
                    assertThat(types.content()).isIn("집", "직장");
                });
        }

        @Test
        @DisplayName("중복된 값을 요청 데이터에 포함하면 예외가 발생한다.")
        void duplicateTypes() {
            // given
            long userId = 1L;
            User user = mockUser(userId);
            when(userQueryService.findById(userId)).thenReturn(user);

            List<Long> duplicateType = List.of(1L, 1L);
            UserPlaceTypesRequest request = new UserPlaceTypesRequest(duplicateType);

            // when & then
            assertThatThrownBy(() -> placeTypeService.updateUserPlaceTypes(userId, request))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(TypeException.DUPLICATE_TYPES);
        }

        @Test
        @DisplayName("존재하지 않는 값이면 예외가 발생한다.")
        void typeNotFound() {
            // given
            long userId = 1L;
            User user = mockUser(userId);
            when(userQueryService.findById(userId)).thenReturn(user);

            long invalidTypeId = 10L;

            List<Long> invalidType = List.of(invalidTypeId);

            when(placeTypeRepository.findAllById(invalidType)).thenReturn(List.of());
            UserPlaceTypesRequest request = new UserPlaceTypesRequest(invalidType);

            // when & then
            assertThatThrownBy(() -> placeTypeService.updateUserPlaceTypes(userId, request))
                .isInstanceOf(GeneralException.class)
                .extracting("exception")
                .isEqualTo(TypeException.TYPE_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("장소 타입 전체 조회 시 ")
    class GetAllPlaceTypesTest {

        @Test
        @DisplayName("성공한다.")
        void getAllPlaceTypes() {
            // given
            PlaceType placeType1 = new PlaceType(1L,"집");
            PlaceType placeType2 = new PlaceType(2L, "학교");
            PlaceType placeType3 = new PlaceType(3L, "직장");

            when(placeTypeRepository.findAll()).thenReturn(List.of(placeType1, placeType2, placeType3));

            // when
            PlaceTypeTotalResponse result = placeTypeService.getAllPlaceTypes();

            // then
            assertThat(result.placeTypes())
                .hasSize(3)
                .extracting(PlaceTypeTotalResponse.PlaceTypeResponse::content)
                .containsExactlyInAnyOrder("집", "학교", "직장");
        }

        @Test
        @DisplayName("장소 타입이 존재하지 않으면 빈 리스트를 반환한다.")
        void emptyRequest() {
            // given
            when(placeTypeRepository.findAll()).thenReturn(List.of());

            // when
            PlaceTypeTotalResponse result = placeTypeService.getAllPlaceTypes();

            // then
            assertThat(result.placeTypes()).isEmpty();
        }
    }
}
