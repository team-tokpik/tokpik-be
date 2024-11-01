package org.example.tokpik_be.type.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.tokpik_be.common.BaseTimeEntity;

@Table(name = "talk_topic_types")
@Getter
@Setter
@Entity
@NoArgsConstructor
public class TopicType extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    public TopicType(String content) {
        this.content = content;
    }
}
