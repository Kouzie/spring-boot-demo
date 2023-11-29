package com.example.mapper.controller;

import com.example.mapper.request.Message;
import com.example.mapper.request.MyCustomValidDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/valid")
@RequiredArgsConstructor
public class ValidController {
    private final ObjectMapper objectMapper;

    @PostMapping
    public String testValid(@RequestBody @Valid MyCustomValidDto validDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(validDto);
    }

    @PostMapping("/message")
    public String testMessage(@RequestBody @Valid Message message) throws JsonProcessingException {
        return objectMapper.writeValueAsString(message);
    }
}
