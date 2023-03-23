package com.example.vending_batch.repository;

import com.example.vending_batch.model.PaycoPaymentInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface PaycoPaymentInfoRepository extends PagingAndSortingRepository<PaycoPaymentInfo, Long> {

    Page<PaycoPaymentInfo> findAllByIdGreaterThan(Long id, Pageable pageable);

    Page<PaycoPaymentInfo> findAllByStatusIsNotLikeAndCreateTimeBefore(String status, Date date, Pageable pageable);


}
