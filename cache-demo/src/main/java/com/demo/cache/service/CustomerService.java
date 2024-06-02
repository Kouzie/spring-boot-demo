package com.demo.cache.service;

import com.demo.cache.CustomerGenerator;
import com.demo.cache.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    private static final String CACHE_MANAGER = "ehCacheManager";
    public static Random random = new Random();

    @Cacheable(value = "customerCache", key = "#input")
    public String getTest(String input) throws InterruptedException {
        Thread.sleep(5000);
        // input을 이용한 계산 또는 데이터 로딩 등의 작업 수행
        return "hello world";
    }

    //캐시 사용
    @Cacheable(value = "customerCache", cacheManager = CACHE_MANAGER)
    public List<Customer> findAll() throws InterruptedException {
        // key 이름: customerCache::SimpleKey []
        Thread.sleep(5000);
        List<Customer> result = new ArrayList<>();
        for (int i = 0; i < random.nextInt(10); i++) {
            result.add(CustomerGenerator.random());
        }
        return result;
    }

    @Cacheable(value = "customerCache", cacheManager = CACHE_MANAGER)
    public List<Customer> findAll(List<String> ids) throws InterruptedException {
        // key 이름: customerCache::1,2,3,4,5
        Thread.sleep(5000);
        List<Customer> result = new ArrayList<>();
        for (String id : ids) {
            result.add(CustomerGenerator.random(id));
        }
        return result;
    }

    //캐시 키값 사용
    @Cacheable(value = "customerCache", key = "#id", cacheManager = CACHE_MANAGER)
    public Customer findById(String id) throws InterruptedException {
        Thread.sleep(5000);
        return CustomerGenerator.random(id);
    }

    //캐시 사용 X
    public Customer findByIdWithoutCache(String id) throws InterruptedException {
        Thread.sleep(5000);
        return CustomerGenerator.random(id);
    }

    //캐시 삭제
    @CacheEvict(value = "customerCache", cacheManager = CACHE_MANAGER)
    public void refresh() {
        log.info("cache clear");
    }

    //캐시 삭제 - 키값 사용
    @CacheEvict(value = "customerCache", key = "#id", cacheManager = CACHE_MANAGER)
    public void refresh(String id) {
        log.info("cache clear");
    }
}
