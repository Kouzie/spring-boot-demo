package com.example.aws.controller;


import com.example.aws.model.Customer;
import com.example.aws.model.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("customer")
public class CustomerController {
    private final CustomerService service;

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable String id){
        Customer customer = service.findById(id).orElseThrow(() -> new IllegalArgumentException("not found customer, id(" + id + ")"));
        return customer;
    }

    @PostMapping
    public Customer addCustomer(@RequestBody Customer customer) {
        return service.save(customer);
    }

    @DeleteMapping("/{id}")
    public void removeCustomer(@PathVariable String id){
        Customer customer = service.findById(id).orElseThrow(() -> new IllegalArgumentException("not found customer, id(" + id + ")"));
        service.delete(customer);
        return;
    }
}
