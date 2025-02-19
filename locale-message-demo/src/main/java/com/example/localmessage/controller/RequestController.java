package com.example.localmessage.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class RequestController {

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
}
