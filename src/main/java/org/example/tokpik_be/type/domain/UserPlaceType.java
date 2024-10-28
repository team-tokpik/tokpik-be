package org.example.tokpik_be.type.domain;

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

@Table(name = "user_talk_place_types")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPlaceType extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private long userId;

    @ManyToOne
    @JoinColumn(name = "talk_place_type_id")
    private PlaceType placeType;

    public UserPlaceType(long userId, PlaceType placeType) {
        this.userId = userId;
        this.placeType = placeType;
    }
}
