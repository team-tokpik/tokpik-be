package org.example.tokpik_be.type.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;

import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.TypeException;
import org.example.tokpik_be.exception.UserException;
import org.example.tokpik_be.support.ControllerTestSupport;
import org.example.tokpik_be.type.dto.request.UserPlaceTypesRequest;
import org.example.tokpik_be.type.dto.response.PlaceTypeTotalResponse;
import org.example.tokpik_be.type.dto.response.UserPlaceTypeResponse;
import org.example.tokpik_be.type.service.PlaceTypeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PlaceTypeControllerTest extends ControllerTestSupport {
    @Mock
    private PlaceTypeService placeTypeService;
    @InjectMocks
    private PlaceTypeController placeTypeController;
    @Override
    protected Object initController(){
        return placeTypeController;
    }
    private final long userId = 1L;
    @Nested
    @DisplayName("사용자 장소 태그 조회 시 ")
    class GetUserPlaceTypesTest{
        @Test
        @DisplayName("성공한다.")
        void success() throws Exception {
            // Given
            UserPlaceTypeResponse response = new UserPlaceTypeResponse(userId, List.of(
                new UserPlaceTypeResponse.PlaceTypeDTO(1L, "Type 1")
            ));
            given(placeTypeService.getUserPlaceTypes(userId)).willReturn(response);

            // When
            ResultActions resultActions = mockMvc.perform(get("/users/place-types").requestAttr("userId", userId));

            // Then
            resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.placeTopicTypes[0].id").value(1L))
                .andExpect(jsonPath("$.placeTopicTypes[0].content").value("Type 1"));
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외가 발생한다.")
        void userNotFound() throws Exception {
            // Given
            given(placeTypeService.getUserPlaceTypes(userId))
                .willThrow(new GeneralException(UserException.USER_NOT_FOUND));

            // When
            ResultActions resultActions = mockMvc.perform(get("/users/place-types").requestAttr("userId", userId));

            // Then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(UserException.USER_NOT_FOUND.getMessage()));
        }
    }

    @Nested
    @DisplayName("사용자 장소 태그 수정 시 ")
    class UpdateUserPlaceTypesTest {
        @Test
        @DisplayName("성공한다.")
        void success() throws Exception {
            // Given
            UserPlaceTypesRequest request = new UserPlaceTypesRequest(List.of(1L, 2L));
            UserPlaceTypeResponse response = new UserPlaceTypeResponse(userId, List.of(
                new UserPlaceTypeResponse.PlaceTypeDTO(1L, "Type 1"),
                new UserPlaceTypeResponse.PlaceTypeDTO(2L, "Type 2")
            ));
            given(placeTypeService.updateUserPlaceTypes(eq(userId), any(UserPlaceTypesRequest.class))).willReturn(response);

            // When
            ResultActions resultActions = mockMvc.perform(patch("/users/place-types")
                .requestAttr("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)));

            // Then
            resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.placeTopicTypes[0].id").value(1L))
                .andExpect(jsonPath("$.placeTopicTypes[0].content").value("Type 1"))
                .andExpect(jsonPath("$.placeTopicTypes[1].id").value(2L))
                .andExpect(jsonPath("$.placeTopicTypes[1].content").value("Type 2"));
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외가 발생한다.")
        void userNotFound() throws Exception {
            // Given
            given(placeTypeService.getUserPlaceTypes(userId))
                .willThrow(new GeneralException(UserException.USER_NOT_FOUND));

            // When
            ResultActions resultActions = mockMvc.perform(get("/users/place-types")
                .requestAttr("userId", userId));

            // Then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(UserException.USER_NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("요청 데이터가 없으면 예외가 발생한다.")
        void noRequestData() throws Exception {
            // Given
            given(placeTypeService.updateUserPlaceTypes(anyLong(), any(UserPlaceTypesRequest.class)))
                .willThrow(new GeneralException(TypeException.INVALID_REQUEST));

            // When
            ResultActions resultActions = mockMvc.perform(patch("/users/place-types")
                .requestAttr("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));

            // Then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(TypeException.INVALID_REQUEST.getMessage()));
        }
    }
    @Nested
    @DisplayName("장소 태그 전체 조회 시 ")
    class getAllTypesTest{
        @Test
        @DisplayName("성공한다.")
        void success() throws Exception{
            // Given
            PlaceTypeTotalResponse response = new PlaceTypeTotalResponse(List.of(
                new PlaceTypeTotalResponse.PlaceTypeResponse(1L, "Type 1"),
                new PlaceTypeTotalResponse.PlaceTypeResponse(2L, "Type 2")
            ));
            given(placeTypeService.getAllPlaceTypes()).willReturn(response);

            // When
            ResultActions resultActions = mockMvc.perform(get("/place-types"));

            // Then
            resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placeTypes[0].content").value("Type 1"))
                .andExpect(jsonPath("$.placeTypes[1].content").value("Type 2"));
        }
    }
}
