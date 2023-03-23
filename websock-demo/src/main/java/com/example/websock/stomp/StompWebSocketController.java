package com.example.websock.stomp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/stomp")
public class StompWebSocketController {

    @GetMapping("/sample")
    public void websocket(Model model) {
        log.info("websocket called...");
        model.addAttribute("result", "SUCCESS");
    }

    @GetMapping("/sockjs")
    public String websocketSockJs(Model model) {
        log.info("websocketSockJs called...");
        model.addAttribute("result", "SUCCESS");
        return "stomp/sample-with-sockjs.html";
    }
}
