package com.example.websock.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/spring")
public class SpringWebSocketController {

    @GetMapping("/sample")
    public void websocket(Model model) {
        log.info("websocket called...");
        model.addAttribute("result", "SUCCESS");
    }

    @GetMapping("/sockjs")
    public String websocketSockJs(Model model) {
        log.info("websocketSockJs called...");
        model.addAttribute("result", "SUCCESS");
        return "spring/sample-with-sockjs";
    }
}
