package org.example.tokpik_be.type.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TypeException;
import org.example.tokpik_be.support.ServiceTestSupport;
import org.example.tokpik_be.type.domain.PlaceType;
import org.example.tokpik_be.type.domain.UserPlaceType;
import org.example.tokpik_be.type.dto.request.UserPlaceTypesRequest;
import org.example.tokpik_be.type.dto.response.PlaceTypeTotalResponse;
import org.example.tokpik_be.type.dto.response.UserPlaceTypeResponse;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

public class PlaceTypeServiceTest extends ServiceTestSupport {
    private PlaceTypeService placeTypeService;

    @MockBean
    private UserQueryService userQueryService;

    @BeforeEach
    void setUp() {
        placeTypeService = new PlaceTypeService(userPlaceTypeRepository, userQueryService, placeTypeRepository);
    }

    @Nested
    @DisplayName("사용자 장소 태그 조회 시 ")
    class GetPlaceTypesTest {

        @Test
        @DisplayName("성공한다.")
        void getMyPlaceTypes() {
            // Given
            User user = new User("test@test.com", "profile-photo/1");
            userRepository.save(user);
            em.flush();
            Long userId = user.getId();

            PlaceType placeType1 = new PlaceType("집");
            PlaceType placeType2 = new PlaceType("회사");
            placeTypeRepository.saveAll(Arrays.asList(placeType1, placeType2));

            UserPlaceType userPlaceType1 = new UserPlaceType(userId, placeType1);
            UserPlaceType userPlaceType2 = new UserPlaceType(userId, placeType2);
            userPlaceTypeRepository.saveAll(Arrays.asList(userPlaceType1, userPlaceType2));

            em.flush();
            em.clear();

            when(userQueryService.findById(userId)).thenReturn(user);

            // When
            UserPlaceTypeResponse response = placeTypeService.getUserPlaceTypes(userId);

            // Then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.placeTopicTypes())
                .hasSize(2)
                .map(UserPlaceTypeResponse.PlaceTypeDTO::content)
                .containsExactlyInAnyOrder("집", "회사");

            assertThat(response.placeTopicTypes())
                .map(UserPlaceTypeResponse.PlaceTypeDTO::id)
                .doesNotContainNull();

        }
    }

    @Nested
    @DisplayName("사용자 장소 태그 수정 시 ")
    class UpdatePlaceTypesTest {
        @Test
        @DisplayName("성공한다.")
        void updateMyPlaceTypes() {
            // Given
            User user = new User("test@test.com", "http://test.com/photo.jpg");
            userRepository.save(user);
            em.flush();
            Long userId = user.getId();

            PlaceType placeType1 = new PlaceType("집");
            PlaceType placeType2 = new PlaceType("학교");
            PlaceType placeType3 = new PlaceType("직장");
            placeTypeRepository.saveAll(Arrays.asList(placeType1, placeType2, placeType3));

            UserPlaceType userPlaceType1 = new UserPlaceType(userId, placeType1);
            UserPlaceType userPlaceType2 = new UserPlaceType(userId, placeType2);
            userPlaceTypeRepository.saveAll(Arrays.asList(userPlaceType1, userPlaceType2));

            em.flush();
            em.clear();

            when(userQueryService.findById(userId)).thenReturn(user);

            List<Long> newTypeIds = Arrays.asList(placeType1.getId(), placeType3.getId());
            UserPlaceTypesRequest request = new UserPlaceTypesRequest(newTypeIds);

            // When
            UserPlaceTypeResponse response = placeTypeService.updateUserPlaceTypes(userId, request);

            // Then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.placeTopicTypes())
                .hasSize(2)
                .allSatisfy(types -> {
                    assertThat(types.content()).isIn("집", "직장");
                    assertThat(types.id()).isIn(placeType1.getId(), placeType3.getId());
                });
        }


        @Test
        @DisplayName("중복된 값을 요청 데이터에 포함하면 예외가 발생한다.")
        void duplicateTypes() {
            // Given
            User user = new User("test@test.com", "http://test.com/photo.jpg");
            userRepository.save(user);
            em.flush();
            Long userId = user.getId();

            PlaceType placeType1 = new PlaceType("집");
            placeTypeRepository.save(placeType1);

            em.flush();
            em.clear();

            when(userQueryService.findById(userId)).thenReturn(user);

            List<Long> duplicateTypeIds = Arrays.asList(placeType1.getId(), placeType1.getId());
            UserPlaceTypesRequest request = new UserPlaceTypesRequest(duplicateTypeIds);

            // Then
            assertThatThrownBy(() -> placeTypeService.updateUserPlaceTypes(userId, request))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(TypeException.DUPLICATE_TYPES.getMessage());
        }

        @Test
        @DisplayName("존재하지 않는 값이면 예외가 발생한다.")
        void typeNotFound() {
            // Given
            User user = new User("test@test.com", "http://test.com/photo.jpg");
            userRepository.save(user);
            em.flush();
            Long userId = user.getId();

            PlaceType placeType1 = new PlaceType("집");
            placeTypeRepository.save(placeType1);

            em.flush();
            em.clear();

            when(userQueryService.findById(userId)).thenReturn(user);


            List<Long> invalidTypeIds = Arrays.asList(placeType1.getId(), 10L);
            UserPlaceTypesRequest request = new UserPlaceTypesRequest(invalidTypeIds);

            // Then
            assertThatThrownBy(() -> placeTypeService.updateUserPlaceTypes(userId, request))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(TypeException.TYPE_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("장소 태그 전체 조회 시 ")
    class GetAllPlaceTypesTest {

        @Test
        @DisplayName("성공한다.")
        void getAllPlaceTypes() {

            // Given
            PlaceType placeType1 = new PlaceType("집");
            PlaceType placeType2 = new PlaceType("학교");
            PlaceType placeType3 = new PlaceType("직장");
            placeTypeRepository.saveAll(Arrays.asList(placeType1, placeType2, placeType3));

            em.flush();
            em.clear();

            // When
            PlaceTypeTotalResponse result = placeTypeService.getAllPlaceTypes();

            // Then
            assertThat(result.placeTypes())
                .hasSize(3)
                .satisfies(types -> {
                    assertThat(types)
                        .extracting(PlaceTypeTotalResponse.PlaceTypeResponse::content)
                        .containsExactlyInAnyOrder("집", "학교", "직장");
                    assertThat(types)
                        .extracting(PlaceTypeTotalResponse.PlaceTypeResponse::placeTypeId)
                        .doesNotContainNull();
                });
        }

        @Test
        @DisplayName("장소 태그가 존재하지 않으면 빈 리스트를 반환한다.")
        void emptyRequest() {
            // When
            PlaceTypeTotalResponse result = placeTypeService.getAllPlaceTypes();

            // Then
            assertThat(result.placeTypes()).isEmpty();
        }
    }
}
