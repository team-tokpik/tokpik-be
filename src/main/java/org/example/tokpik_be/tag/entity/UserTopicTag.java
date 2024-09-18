package org.example.tokpik_be.tag.entity;

import org.example.tokpik_be.common.BaseTimeEntity;
import org.example.tokpik_be.tag.domain.TopicTag;
import org.example.tokpik_be.user.domain.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "user_talk_topic_tags")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserTopicTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "talk_topic_tag_id", nullable = false)
    private TopicTag topicTag;

    public UserTopicTag(User user, TopicTag topicTag) {
        this.user = user;
        this.topicTag = topicTag;
    }

}
