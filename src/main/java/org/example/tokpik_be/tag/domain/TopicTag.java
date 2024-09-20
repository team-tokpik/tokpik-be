package org.example.tokpik_be.tag.domain;

import org.example.tokpik_be.common.BaseTimeEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "talk_topic_tags")
@Getter
@Setter
@Entity
@NoArgsConstructor
public class TopicTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    public TopicTag(Long id, String content) {
        this.id = id;
        this.content = content;
    }
}
