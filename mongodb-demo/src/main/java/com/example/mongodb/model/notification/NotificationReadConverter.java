package com.example.mongodb.model.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

@RequiredArgsConstructor
public class NotificationReadConverter implements Converter<Document, NotificationDocument> {

    private final ObjectMapper objectMapper;
    @Override
    public NotificationDocument convert(Document source) {
        NotificationDocument result = objectMapper.convertValue(source, NotificationDocument.class);
        return result;
    }
}