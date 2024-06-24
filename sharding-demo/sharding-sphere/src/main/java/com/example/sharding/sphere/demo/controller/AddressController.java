package com.example.sharding.sphere.demo.controller;


import com.example.sharding.sphere.demo.dto.AddressDto;
import com.example.sharding.sphere.demo.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public List<AddressDto> getOrders() {
        return addressService.getAllAddress();
    }

    @PostMapping
    public AddressDto addRandomAddress() {
        return addressService.addRandomAccount();
    }
}
