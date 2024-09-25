package org.example.tokpik_be.talk_topic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tokpik_be.user.converter.GenderConverter;
import org.example.tokpik_be.user.enums.Gender;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TalkPartner {

    @Column(name = "partner_gender")
    @Convert(converter = GenderConverter.class)
    private Gender gender;

    @Column(name = "partner_age_lower_bound")
    private Integer ageLowerBound;

    @Column(name = "partner_age_upper_bound")
    private Integer ageUpperBound;
}
