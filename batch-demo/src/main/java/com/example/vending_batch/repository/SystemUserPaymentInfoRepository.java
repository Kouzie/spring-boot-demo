package com.example.vending_batch.repository;

import com.example.vending_batch.model.PaycoPaymentInfo;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SystemUserPaymentInfoRepository extends PagingAndSortingRepository<PaycoPaymentInfo, Long> {
}
