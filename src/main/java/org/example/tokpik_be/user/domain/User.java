package org.example.tokpik_be.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tokpik_be.common.BaseTimeEntity;
import org.example.tokpik_be.tag.entity.UserPlaceTag;
import org.example.tokpik_be.tag.entity.UserTopicTag;
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

    private String refreshToken;
    private LocalDate birth;

    @Convert(converter = GenderConverter.class)
    private Gender gender;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<UserTopicTag> userTopicTags = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<UserPlaceTag> userPlaceTags = new ArrayList<>();

    public User(String email, String profilePhotoUrl) {
        this.email = email;
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public boolean requiresProfile() {

        return Objects.isNull(birth);
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateProfile(LocalDate birth, Gender gender) {
        this.birth = birth;
        this.gender = gender;
    }

    public boolean notEqualRefreshToken(String refreshToken) {

        return !this.refreshToken.equals(refreshToken);
    }

    public void updateUserTopicTags(List<UserTopicTag> userTopicTags) {
        this.userTopicTags.addAll(userTopicTags);
    }

    public void updateUserPlaceTags(List<UserPlaceTag> userPlaceTags) {
        this.userPlaceTags.addAll(userPlaceTags);
    }
}
