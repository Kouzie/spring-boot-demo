package com.example.redis.service;

import com.example.redis.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.redis.RedisDemoApplication.random;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    //캐시 사용
    @Cacheable(value = "customerCache", cacheManager = "userCacheManager")
    public List<Customer> findAll() throws InterruptedException {
        // key 이름: customerCache::SimpleKey []
        Thread.sleep(5000);
        List<Customer> result = new ArrayList<>();
        for (int i = 0; i < random.nextInt(10); i++) {
            result.add(CustomerGenerator.random());
        }
        return result;
    }

    @Cacheable(value = "customerCache", cacheManager = "userCacheManager")
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
    @Cacheable(value = "customerCache", key="#id")
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
    @CacheEvict(value = "customerCache")
    public void refresh() {
        log.info("cache clear");
    }

    //캐시 삭제 - 키값 사용
    @CacheEvict(value = "customerCache", key="#id")
    public void refresh(String id) {
        log.info("cache clear");
    }
}
