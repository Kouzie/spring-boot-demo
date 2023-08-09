package com.example.jpa.controller.order;

import com.example.jpa.dto.OrderDto;
import com.example.jpa.model.id.OrderId;
import com.example.jpa.model.order.Order;
import com.example.jpa.model.order.OrderState;
import com.example.jpa.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{id}/{lockType}")
    public Order getOrderByIdPessimistic(@PathVariable String id, @PathVariable String lockType) {
        OrderId orderId = new OrderId(id);
        return orderService.findById(orderId, lockType);
    }

    @GetMapping("/state/{state}/{lockType}")
    public List<OrderDto> getOrderByStatePessimistic(@PathVariable String state, @PathVariable String lockType) {
        OrderState orderState = OrderState.valueOf(state);
        return orderService.findAllByOrderState(orderState, lockType);
    }

    @PatchMapping("/{id}/{lockType}")
    public Order patchOrder(@PathVariable String id, @RequestBody OrderDto orderDto, @PathVariable String lockType) {
        return orderService.patch(new OrderId(id), lockType, orderDto);
    }

    @PostMapping("/random")
    public Order addOrder() {
        return orderService.random();
    }

}
