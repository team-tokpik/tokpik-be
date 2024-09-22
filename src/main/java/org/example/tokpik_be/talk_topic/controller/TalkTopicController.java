package org.example.tokpik_be.talk_topic.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.talk_topic.dto.request.TalkTopicSearchRequest;
import org.example.tokpik_be.talk_topic.dto.response.TalkTopicSearchResponse;
import org.example.tokpik_be.talk_topic.service.TalkTopicQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "대화 주제 API", description = "대화 주제 연관 API")
@RestController
@RequiredArgsConstructor
public class TalkTopicController {

    private final TalkTopicQueryService talkTopicQueryService;

    @GetMapping("/topics")
    public ResponseEntity<List<TalkTopicSearchResponse>> searchTalkTopics(
        @RequestAttribute("userId") long userId,
        @RequestBody TalkTopicSearchRequest request) {

        List<TalkTopicSearchResponse> response = talkTopicQueryService
            .searchTalkTopics(userId, request);

        return ResponseEntity.ok().body(response);
    }
}
