package com.example.mapper.repository;


import com.example.mapper.modal.Car;
import org.springframework.stereotype.Repository;

@Repository
public class CarRepository {
    public Car save(Car car) {
        return car;
    }

    public Car findById(Long id) {
        Car car = null;
        return car;
    }
}
