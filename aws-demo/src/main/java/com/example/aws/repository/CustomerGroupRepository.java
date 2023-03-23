package com.example.aws.repository;


import com.example.aws.model.CustomerGroup;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.EnableScanCount;
import org.springframework.data.repository.PagingAndSortingRepository;

@EnableScan
@EnableScanCount
public interface CustomerGroupRepository extends PagingAndSortingRepository<CustomerGroup, String> {
}