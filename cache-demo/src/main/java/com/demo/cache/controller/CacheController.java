package com.demo.cache.controller;

import com.demo.cache.model.Customer;
import com.demo.cache.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
public class CacheController {
    private final CustomerService customerService;

    @GetMapping("/test")
    public String getTest() throws InterruptedException {
        return customerService.getTest("test");
    }

    @GetMapping
    public List<Customer> getAll(@RequestParam(required = false) List<String> ids) throws InterruptedException {
        if (ids != null && ids.size() != 0)
            return customerService.findAll(ids);
        else
            return customerService.findAll();
    }

    @GetMapping("/{id}")
    public Customer getById(@PathVariable String id) throws InterruptedException {
        return customerService.findById(id);
    }

    @GetMapping("/{id}/without")
    public Customer getByIdWithoutCache(@PathVariable String id) throws InterruptedException {
        return customerService.findByIdWithoutCache(id);
    }

    @DeleteMapping
    public void deleteCache() {
        customerService.refresh();
    }

    @DeleteMapping("/{id}")
    public void deleteCacheById(@PathVariable String id) {
        customerService.refresh(id);
    }
}
