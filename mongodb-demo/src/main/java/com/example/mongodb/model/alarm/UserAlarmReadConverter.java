package com.example.mongodb.model.alarm;

import com.example.mongodb.model.alarm.UserAlarmDocument.UserAlarm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

@RequiredArgsConstructor
public class UserAlarmReadConverter implements Converter<Document, UserAlarm> {

    private final ObjectMapper objectMapper;

    @Override
    public UserAlarm convert(Document source) {
        UserAlarm result = objectMapper.convertValue(source, UserAlarm.class);
        return result;
    }
}