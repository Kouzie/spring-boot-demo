package com.example.jpa.order;

import com.example.jpa.order.model.OrderRepository;
import com.example.jpa.order.model.id.OrderId;
import com.example.jpa.order.model.Order;
import com.example.jpa.order.model.OrderState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Order findById(OrderId id, String lockType) {
        Order result;
        if (lockType.equals("pessimistic")) {
            result = orderRepository.findByIdPessimistic(id)
                    .orElseThrow(() -> new IllegalArgumentException("Not Found Order, id:" + id));

        } else if (lockType.equals("optimistic")) {
            result = orderRepository.findByIdOptimistic(id)
                    .orElseThrow(() -> new IllegalArgumentException("Not Found Order, id:" + id));
        } else {
            throw new IllegalArgumentException("unknown lock type");
        }
        log.info("find by id end");
        return result;
    }

    @Transactional(readOnly = true)
    public List<OrderDto> findAllByOrderState(OrderState state, String lockType) {
        if (lockType.equals("pessimistic")) {
            return orderRepository.findAllByOrderStatePessimistic(state).stream()
                    .map(order -> new OrderDto(order.getOrderId().getNumber(), order.getState(), order.getVersion()))
                    .collect(Collectors.toList());
        } else if (lockType.equals("optimistic")) {
            return orderRepository.findAllByOrderStateOptimistic(state).stream()
                    .map(order -> new OrderDto(order.getOrderId().getNumber(), order.getState(), order.getVersion()))
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("unknown lock type");
        }
    }

    @Transactional
    public Order patch(OrderId id, String lockType, OrderDto orderDto) {
        Order order;
        if (lockType.equals("pessimistic")) {
            order = orderRepository.findByIdPessimistic(id)
                    .orElseThrow(() -> new IllegalArgumentException("Not Found Order, id:" + id));
        } else if (lockType.equals("optimistic")) {
            order = orderRepository.findByIdOptimistic(id)
                    .orElseThrow(() -> new IllegalArgumentException("Not Found Order, id:" + id));
        } else {
            throw new IllegalArgumentException("unknown lock type");
        }
        order.update(orderDto);
        // order = orderRepository.save(order);
        return order;
    }

    @Transactional
    public Order random() {
        Order o = Order.random();
        return orderRepository.save(o);
    }

}
