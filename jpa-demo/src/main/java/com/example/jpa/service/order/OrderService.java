package com.example.jpa.service.order;

import com.example.jpa.model.id.OrderId;
import com.example.jpa.model.order.Order;
import com.example.jpa.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Order findById(OrderId id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Order, id:" + id));
    }
}
