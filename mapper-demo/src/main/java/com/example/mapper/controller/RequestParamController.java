package com.example.mapper.controller;

import com.example.mapper.request.CustomRequestDto;
import com.example.mapper.request.TestRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
public class RequestParamController {

    private final ObjectMapper mapper;

    @GetMapping("/zone-date/utc")
    public String zoneDateUtc(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime zdt) {
        zdt = zdt.withZoneSameInstant(ZoneId.of("UTC"));
        return zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @GetMapping(value = "/zone-date/param")
    public String testGet(TestRequestDto request) throws JsonProcessingException {
        return mapper.writeValueAsString(request);
    }

    @GetMapping("/alias/param")
    public String testSimpleParam(CustomRequestDto request) throws JsonProcessingException {
        return mapper.writeValueAsString(request);
    }
}
