package org.example.tokpik_be.scrap.domain;

import org.example.tokpik_be.common.BaseTimeEntity;
import org.example.tokpik_be.talk_topic.domain.TalkTopic;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "scraped_talk_topics")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScrapTopic extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scrap_id", nullable = false)
    private Scrap scrap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talk_topic_id", nullable = false)
    private TalkTopic talkTopic;

    public ScrapTopic(Scrap scrap, TalkTopic talkTopic){
        this.scrap = scrap;
        this.talkTopic = talkTopic;
    }
}
