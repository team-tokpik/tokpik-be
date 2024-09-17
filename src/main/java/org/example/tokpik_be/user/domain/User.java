package org.example.tokpik_be.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tokpik_be.common.BaseTimeEntity;
import org.example.tokpik_be.user.converter.GenderConverter;
import org.example.tokpik_be.user.enums.Gender;

@Table(name = "users")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String profilePhotoUrl;

    @Column(nullable = false)
    private String kakaoId;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime birth;

    @Column(nullable = false)
    @Convert(converter = GenderConverter.class)
    private Gender gender;


    public User(String email,
        String profilePhotoUrl,
        String kakaoId,
        String refreshToken,
        LocalDateTime birth,
        Gender gender) {
        this.email = email;
        this.profilePhotoUrl = profilePhotoUrl;
        this.kakaoId = kakaoId;
        this.refreshToken = refreshToken;
        this.birth = birth;
        this.gender = gender;
    }
}
