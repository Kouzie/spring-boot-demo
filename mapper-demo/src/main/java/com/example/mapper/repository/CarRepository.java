package com.example.mapper.repository;


import com.example.mapper.modal.Car;
import com.example.mapper.modal.CarType;
import org.springframework.stereotype.Repository;

@Repository
public class CarRepository {
    public Car save(Car car) {
        return car;
    }

    public Car findById(Long id) {
        Car car = new Car();
        car.setId(id);
        car.setMake("Hyundai");
        car.setType(CarType.COUPE);
        car.setNumberOfSeats(4);
        return car;
    }
}
