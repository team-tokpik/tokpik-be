package org.example.tokpik_be.type.domain;

import jakarta.persistence.Column;
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
import org.example.tokpik_be.common.BaseTimeEntity;

@Table(name = "user_talk_topic_types")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserTopicType extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private long userId;

    @ManyToOne
    @JoinColumn(name = "talk_topic_type_id", nullable = false)
    private TopicType topicType;

    public UserTopicType(long userId, TopicType topicType) {
        this.userId = userId;
        this.topicType = topicType;
    }
}
