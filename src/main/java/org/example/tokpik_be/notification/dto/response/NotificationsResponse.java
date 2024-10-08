package org.example.tokpik_be.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record NotificationsResponse(
    @Schema(type = "array", description = "조회된 알림들")
    List<NotificationResponse> contents,

    @Schema(type = "number", description = "다음 페이지 첫 알림 ID, null일 경우 마지막 페이지", example = "1")
    Long nextCursorId,

    @Schema(type = "boolean", description = "첫 페이지 여부", example = "true")
    boolean first,

    @Schema(type = "boolean", description = "마지막 페이지 여부", example = "false")
    boolean last
) {

    public record NotificationResponse(
        @Schema(type = "number", description = "알림 ID", example = "1")
        long notificationId,

        @Schema(type = "string", description = "알림 일자", example = "2024-09-11")
        LocalDate noticeDate,

        @Schema(type = "string", description = "알림 시작 시간", example = "09:00")
        LocalTime notificationStartTime,

        @Schema(type = "string", description = "알림 종료 시간", example = "10:00")
        LocalTime notificationEndTime,

        @Schema(type = "number", description = "알림 간격(분 단위)", example = "30")
        int notificationInterval,

        @Schema(type = "string", description = "알림 이름", example = "부장님 개그 모음")
        String notificationName,

        @Schema(type = "number", description = "스크랩에 저장된 대화 주제 합계", example = "30")
        long notificationTopicTotal,

        @Schema(type = "array", description = "알림 지정 순서에 따른 첫 네 대화 주제 종류들")
        List<NotificationTalkTopicTypeResponse> notificationTalkTopicTypes
    ) {


        public record NotificationTalkTopicTypeResponse(
            @Schema(type = "number", description = "대화 주제 종류 ID", example = "1")
            long topicTypeId,

            @Schema(type = "string", description = "대화 주제 종류 내용", example = "비즈니스")
            String topicTypeContent
        ) {

        }
    }
}
