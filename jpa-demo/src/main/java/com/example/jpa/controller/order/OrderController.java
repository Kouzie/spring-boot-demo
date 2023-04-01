package com.example.jpa.controller.order;

import com.example.jpa.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

//    @GetMapping("/{id}")
//    public Order getBoardById(@PathVariable String id) {
//        return orderService.findById(new OrderId(id));
//    }
}
