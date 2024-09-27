package org.example.tokpik_be.talk_topic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tokpik_be.tag.domain.PlaceTag;
import org.example.tokpik_be.tag.domain.TopicTag;

@Table(name = "talk_topics")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TalkTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String subtitle;

    @Column(nullable = false)
    private String situation;

    @Embedded
    private TalkPartner partner;

    @ManyToOne
    @JoinColumn(name = "talk_topic_tag_id")
    private TopicTag topicTag;

    @ManyToOne
    @JoinColumn(name = "talk_place_tag_id")
    private PlaceTag placeTag;

    public TalkTopic(String title,
        String subtitle,
        String situation,
        TalkPartner partner,
        TopicTag topicTag,
        PlaceTag placeTag) {
        this.title = title;
        this.subtitle = subtitle;
        this.situation = situation;
        this.partner = partner;
        this.topicTag = topicTag;
        this.placeTag = placeTag;
    }
}
