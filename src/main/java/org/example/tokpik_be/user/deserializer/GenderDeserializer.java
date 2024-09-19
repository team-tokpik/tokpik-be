package org.example.tokpik_be.user.deserializer;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import org.example.tokpik_be.user.enums.Gender;

public class GenderDeserializer extends JsonDeserializer<Gender> {

    @Override
    public Gender deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
        throws IOException {
        boolean value = jsonParser.getBooleanValue();

        return Gender.from(value);
    }
}
