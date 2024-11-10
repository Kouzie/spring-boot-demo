package com.example.jpa.order;

import com.example.jpa.order.model.id.OrderId;
import com.example.jpa.order.model.Order;
import com.example.jpa.order.model.OrderState;
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
