package com.example.dynamodb.model.service;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.example.dynamodb.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.dynamodb.repository.CustomerRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;

    private final DynamoDBMapper dynamoDBMapper;

    public Optional<Customer> findById(String id) {
        return repository.findById(id);
    }

    public Customer save(Customer customer) {
        return repository.save(customer);
    }


    public void delete(Customer customer) {
        repository.delete(customer);
    }
}