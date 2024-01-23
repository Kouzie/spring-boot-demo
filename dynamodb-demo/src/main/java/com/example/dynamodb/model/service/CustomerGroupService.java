package com.example.dynamodb.model.service;


import com.example.dynamodb.model.CustomerGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.dynamodb.repository.CustomerGroupRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerGroupService {
    private final CustomerGroupRepository repository;

    public Optional<CustomerGroup> findById(String id) {
        return repository.findById(id);
    }

    public CustomerGroup save(CustomerGroup customerGroup) {
        return repository.save(customerGroup);
    }

    public void delete(CustomerGroup customerGroup) {
        repository.delete(customerGroup);
    }
}