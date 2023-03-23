package com.example.websock.tomcat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/tomcat")
public class TomcatWebSocketController {

    @GetMapping("/sample")
    public void websocket_sample(Model model) {
        log.info("websocket_sample called...");
        model.addAttribute("result", "SUCCESS");
    }
}
