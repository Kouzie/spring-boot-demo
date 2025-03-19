package com.example.jpa.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import org.springframework.util.StringUtils;

public class ObjectConverter<T> implements AttributeConverter<T, String> {

    private final ObjectMapper mapper;
    private final Class<T> type;

    public ObjectConverter(ObjectMapper mapper, Class<T> type) {
        this.mapper = mapper;
        this.type = type;
    }


    @Override
    public String convertToDatabaseColumn(T attribute) {
        try {
            if (attribute != null)
                return mapper.writeValueAsString(attribute);
            else return null;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        try {
            if (StringUtils.hasText(dbData))
                return mapper.readValue(dbData, type);
            else
                return null;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
