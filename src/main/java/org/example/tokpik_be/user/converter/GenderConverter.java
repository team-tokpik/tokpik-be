package org.example.tokpik_be.user.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.example.tokpik_be.user.enums.Gender;

@Converter
public class GenderConverter implements AttributeConverter<Gender, Boolean> {

    @Override
    public Boolean convertToDatabaseColumn(Gender gender) {

        return gender.toBoolean();
    }

    @Override
    public Gender convertToEntityAttribute(Boolean dbData) {

        return Gender.from(dbData);
    }
}
