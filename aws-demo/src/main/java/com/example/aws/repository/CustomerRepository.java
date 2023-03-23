package com.example.aws.repository;


import com.example.aws.model.Customer;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.EnableScanCount;

@EnableScan
@EnableScanCount
public interface CustomerRepository extends DynamoDBCrudRepository<Customer, String> {
}