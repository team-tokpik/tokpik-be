package org.example.tokpik_be.tag.entity;

import jakarta.persistence.Column;
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
import org.example.tokpik_be.common.BaseTimeEntity;
import org.example.tokpik_be.tag.domain.PlaceTag;

@Table(name = "user_talk_place_tags")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPlaceTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private long userId;

    @ManyToOne
    @JoinColumn(name = "talk_place_tag_id")
    private PlaceTag placeTag;

    public UserPlaceTag(long userId, PlaceTag placeTag) {
        this.userId = userId;
        this.placeTag = placeTag;
    }
}
