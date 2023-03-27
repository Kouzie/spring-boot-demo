package com.example.jpa.repository.order;

import com.example.jpa.model.id.OrderId;
import com.example.jpa.model.order.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, OrderId> {
}