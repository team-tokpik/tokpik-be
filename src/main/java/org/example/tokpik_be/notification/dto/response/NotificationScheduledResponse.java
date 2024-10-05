package org.example.tokpik_be.notification.dto.response;

import java.time.LocalTime;

public record NotificationScheduledResponse(
    long notificationId,
    long notificationTalkTopicId,
    String receiverToken,
    String talkTopicTitle,
    String talkTopicSubtitle,
    LocalTime startTime,
    LocalTime endTime,
    int intervalMinutes
) {

}
