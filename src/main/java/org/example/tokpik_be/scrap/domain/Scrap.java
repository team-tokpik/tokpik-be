package org.example.tokpik_be.scrap.domain;

import org.example.tokpik_be.common.BaseTimeEntity;
import org.example.tokpik_be.user.domain.User;

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

@Table(name = "scraps")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrap extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Scrap(String title, User user){
        this.title = title;
        this.user = user;
    }
}
