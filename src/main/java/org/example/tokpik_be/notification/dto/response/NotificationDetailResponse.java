package org.example.tokpik_be.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.util.List;

public record NotificationDetailResponse(
    @Schema(type = "string", description = "알림 이름", example = "부장님과 대화할 때 유용한 주제들")
    String notificationName,

    @Schema(type = "string", description = "알림 시작 시간", example = "10:00")
    @JsonFormat(pattern = "HH:mm")
    LocalTime notificationStartTime,

    @Schema(type = "string", description = "알림 종료 시간", example = "12:00")
    @JsonFormat(pattern = "HH:mm")
    LocalTime notificationEndTime,

    @Schema(type = "number", description = "알림 간격(분 단위)", example = "10")
    int notificationIntervalMinutes,

    @Schema(type = "array", description = "알림 대화 주제들")
    List<NotificationTalkTopicResponse> notificationTalkTopics
) {

    public record NotificationTalkTopicResponse(
        @Schema(type = "string", description = "대화 주제 제목", example = "어제 손흥민 골 보셨어요?")
        String talkTopicTitle,

        @Schema(type = "number", description = "대화 주제 종류 ID", example = "1")
        long talkTopicTypeId,

        @Schema(type = "string", description = "대화 주제 종류 내용", example = "취미와 여가생활")
        String talkTopicContent,

        @Schema(type = "string", description = "대화 주제 알림 시간", example = "10:30")
        @JsonFormat(pattern = "HH:mm")
        LocalTime noticeTime
    ) {

    }
}
