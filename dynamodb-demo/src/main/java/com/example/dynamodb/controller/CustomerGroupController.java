package com.example.dynamodb.controller;


import com.example.dynamodb.model.CustomerGroup;
import com.example.dynamodb.model.service.CustomerGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("customer-group")
public class CustomerGroupController {
    private final CustomerGroupService service;

    @GetMapping("/{id}")
    public CustomerGroup getCustomerGroup(@PathVariable String id) {
        CustomerGroup customerGroup = service.findById(id).orElseThrow(() -> new IllegalArgumentException("not found customer group, id(" + id + ")"));
        return customerGroup;
    }

    @PostMapping
    public CustomerGroup addCustomerGroup(@RequestBody CustomerGroup customerGroup) {
        return service.save(customerGroup);
    }

    @DeleteMapping("/{id}")
    public void removeCustomerGroup(@PathVariable String id) {
        CustomerGroup customerGroup = service.findById(id).orElseThrow(() -> new IllegalArgumentException("not found customer group, id(" + id + ")"));
        service.delete(customerGroup);
        return;
    }
}
