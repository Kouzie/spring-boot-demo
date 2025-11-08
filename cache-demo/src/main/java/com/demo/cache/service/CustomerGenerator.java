package com.demo.cache.service;

import com.demo.cache.model.Customer;
import com.demo.cache.model.CustomerType;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class CustomerGenerator {
    public static Random random = new Random();

    public static Customer random() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID().toString());
        customer.setNickName(randomString());
        customer.setCustomerType(CustomerType.values()[random.nextInt(4)]);
        customer.setCreateTime(new Date());
        return customer;
    }

    public static Customer random(String id) {
        Customer customer = random();
        customer.setId(id);
        return customer;
    }

    public static String randomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }

}
