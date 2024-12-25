package com.example.mapper.controller;

import com.example.mapper.config.FlexibleNamingStrategy;
import com.example.mapper.request.FlexibleDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flexible")
public class FlexibleMapperController {

    // ObjectMapper 인스턴스 생성 및 설정
    private final ObjectMapper mapper;

    public FlexibleMapperController() {
        this.mapper = new ObjectMapper();
        this.mapper.setPropertyNamingStrategy(new FlexibleNamingStrategy()); // camel, snake 모두 지원
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    @PostMapping
    public FlexibleDto getFlexibleObject(@RequestBody String requestBodyString) throws JsonProcessingException {
        FlexibleDto result = mapper.readValue(requestBodyString, FlexibleDto.class);
        return result;
    }
}
