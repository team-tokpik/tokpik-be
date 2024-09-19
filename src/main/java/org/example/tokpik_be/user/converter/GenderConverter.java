package org.example.tokpik_be.user.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Optional;
import org.example.tokpik_be.user.enums.Gender;

@Converter
public class GenderConverter implements AttributeConverter<Gender, Boolean> {

    @Override
    public Boolean convertToDatabaseColumn(Gender gender) {

        return Optional.ofNullable(gender).map(Gender::toBoolean).orElse(null);
    }

    @Override
    public Gender convertToEntityAttribute(Boolean dbData) {

        return Optional.ofNullable(dbData).map(Gender::from).orElse(null);
    }
}
