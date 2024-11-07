package org.example.tokpik_be.type.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tokpik_be.common.BaseTimeEntity;

@Table(name = "talk_place_types")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceType extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    public PlaceType(String content) {
        this.content = content;
    }

    public PlaceType(Long id, String content) {
        this.id = id;
        this.content = content;
    }
}
