package com.example.dynamodb.repository;


import com.example.dynamodb.model.CustomerGroup;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.EnableScanCount;

@EnableScan
@EnableScanCount
public interface CustomerGroupRepository extends DynamoDBCrudRepository<CustomerGroup, String> {
}