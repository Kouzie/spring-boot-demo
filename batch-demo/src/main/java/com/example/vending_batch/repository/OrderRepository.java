package com.example.vending_batch.repository;

import com.example.vending_batch.model.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
}
