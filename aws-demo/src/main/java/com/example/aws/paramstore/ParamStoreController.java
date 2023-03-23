package com.example.aws.paramstore;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ParamStoreController {
    private final ParamStore paramStore;

    @GetMapping("/{value}")
    public String getValue(@PathVariable String value) {
        return paramStore.getProfileValue();
    }
}
