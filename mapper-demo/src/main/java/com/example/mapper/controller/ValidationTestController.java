package com.example.mapper.controller;

import com.example.mapper.request.validation.EnumRequestDto;
import com.example.mapper.request.validation.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/validation")
@RequiredArgsConstructor
public class ValidationTestController {

    private final ObjectMapper mapper;

    @GetMapping("/enum")
    public String validateEnum(@Valid EnumRequestDto requestDto) {
        return requestDto.getType();
    }

    @GetMapping("/custom")
    public String validateCustomMessage(@Valid Message request) throws JsonProcessingException {
        return mapper.writeValueAsString(request);
    }
}
