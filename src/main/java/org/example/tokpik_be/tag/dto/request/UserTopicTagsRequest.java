package org.example.tokpik_be.tag.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTopicTagsRequest {
    private long[] topicTagIds;
}
