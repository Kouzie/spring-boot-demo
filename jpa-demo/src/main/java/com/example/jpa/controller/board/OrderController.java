package com.example.jpa.controller.board;

import com.example.jpa.model.baord.Board;
import com.example.jpa.model.id.OrderId;
import com.example.jpa.model.order.Order;
import com.example.jpa.service.board.BoardService;
import com.example.jpa.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{id}")
    public Order getBoardById(@PathVariable String id) {
        return orderService.findById(new OrderId(id));
    }
}
