package com.example.redis.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/template")
public class RedisTemplateController {
    private final StringRedisTemplate stringRedisTemplate;

    @GetMapping("/sample")
    public void sample(@RequestParam(required = false) String key, Model model) {
        log.info("sample invoked");
        Set<String> keys;
        if (StringUtils.hasText(key)) {
            keys = stringRedisTemplate.keys(key);
        } else {
            keys = stringRedisTemplate.keys("*");
        }
        model.addAttribute("keys", new ArrayList<>(keys));
    }

    @GetMapping("/insert")
    public String insert(@RequestParam String key, @RequestParam String value) {
        log.info("insert invoked, {}:{}", key, value);
        stringRedisTemplate.opsForValue().set(key, value);
        stringRedisTemplate.opsForValue().get(key);
        return "redirect:/template/sample";
    }


    @GetMapping("/deleteAll")
    public String deleteAll(Model model) {
        log.info("deleteAll called()....");
        stringRedisTemplate.delete(stringRedisTemplate.keys("*"));
        return "redirect:/template/sample";
    }
}
