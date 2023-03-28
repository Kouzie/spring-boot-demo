package com.example.swagger.controller;

import com.example.swagger.dto.TestRequest;
import com.example.swagger.dto.TestResponse;
import com.example.swagger.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final TestService service;

    @GetMapping
    public TestResponse getTest(@RequestParam TestRequest request) {
        return service.getResponse(request);
    }

    @PostMapping
    public TestResponse addTest(@RequestBody TestRequest request) {
        return service.getResponse(request);
    }

    @GetMapping("/param")
    public Map<String, Object> getTestParam(@RequestParam(required = false) String id, @RequestParam String name) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        return result;
    }

    @GetMapping("/param/map")
    public Map<String, Object> getTestParam(@RequestParam Map<String, Object> param) {
        return param;
    }

    @GetMapping("/path/{str}")
    public Map<String, Object> getTest(@PathVariable String str) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("str", str);
        return result;
    }
}
