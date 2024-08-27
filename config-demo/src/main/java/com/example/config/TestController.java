package com.example.config;

import com.example.config.config.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final ExtraConfig extraConfig;
    private final FullNameConfig fullNameConfig;
    private final PrefixConfig prefixConfig;
    private final SystemPropertiesConfig systemPropertiesConfig;
    private final EnvConfig envConfig;
    private final ExtConfig extConfig;


    @PostConstruct
    private void init() {
        extraConfig.printConfig();
        fullNameConfig.printConfig();
        prefixConfig.printConfig();
        systemPropertiesConfig.printConfig();
        envConfig.printConfig();
        extConfig.printConfig();
    }
}
