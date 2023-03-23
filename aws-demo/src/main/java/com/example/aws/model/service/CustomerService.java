package com.example.aws.model.service;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.example.aws.model.Customer;
import com.example.aws.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;

    private final DynamoDBMapper dynamoDBMapper;

    public Optional<Customer> findById(String id) {
//        DynamoDBQueryExpression<Customer> expression = new DynamoDBQueryExpression<>();
//        expression.getQueryFilter(Map.of("id", id))
//        dynamoDBMapper.query(Customer.class, expression)
//        return repository.findById(id);
        return null;
    }

    public Customer save(Customer customer) {
//        return repository.save(customer);
        return null;
    }


    public void delete(Customer customer) {
//        repository.delete(customer);
    }
}