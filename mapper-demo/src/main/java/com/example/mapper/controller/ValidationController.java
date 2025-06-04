package com.example.mapper.controller;

import com.example.mapper.request.Message;
import com.example.mapper.request.MyCustomValidDto;
import com.example.mapper.request.UserDataDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/validation")
@RequiredArgsConstructor
public class ValidationController {

    private final ObjectMapper mapper;

    @GetMapping("/test")
    public String getTest() {
        return "hello world!";
    }

    @PostMapping("/data")
    public UserDataDto addData(@Valid @RequestBody UserDataDto requestDto) {
        log.info(requestDto.toString());
        return requestDto;
    }

    @GetMapping("/error")
    public UserDataDto throwError() {
        throw new IllegalArgumentException("unknown server error");
    }

    @PostMapping("/test")
    public String testValid(@Valid @RequestBody MyCustomValidDto validDto) throws JsonProcessingException {
        return mapper.writeValueAsString(validDto);
    }

    @GetMapping("/custom")
    public String validateCustomMessage(@Valid Message request) throws JsonProcessingException {
        return mapper.writeValueAsString(request);
    }
}
