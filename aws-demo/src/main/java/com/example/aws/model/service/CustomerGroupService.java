package com.example.aws.model.service;


import com.example.aws.model.CustomerGroup;
import com.example.aws.repository.CustomerGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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