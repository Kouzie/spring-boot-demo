package com.example.sharding.sphere.demo.controller;

import com.example.sharding.sphere.demo.RandomTestUtil;
import com.example.sharding.sphere.demo.dto.AccountDto;
import com.example.sharding.sphere.demo.dto.OrderDto;
import com.example.sharding.sphere.demo.repository.OrderEntity;
import com.example.sharding.sphere.demo.service.AccountService;
import com.example.sharding.sphere.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final AccountService accountService;

    @GetMapping("/account-id/{accountId}")
    public List<OrderDto> getOrders(@PathVariable Long accountId) {
        return orderService.getAllTables(accountId);
    }

    @GetMapping("/{id}")
    public OrderDto getOrder(@PathVariable Long id) {
        return orderService.getTable(id);
    }

    @PostMapping
    public OrderDto addRandomOrder() {
        AccountDto accountDto = accountService.getRandomAccount();
        return orderService.addRandomOrder(accountDto.getAccountId());
    }
}
