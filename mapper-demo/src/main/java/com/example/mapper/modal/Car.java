package com.example.mapper.modal;

import com.example.mapper.request.CarDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Car {
    private Long id;
    private String make;
    private Integer numberOfSeats;
    private CarType type;

    protected Car() {
    }

    public void update(CarDto car) {
        this.make = car.getMake();
        this.numberOfSeats = car.getSeatCount();
        this.type = CarType.valueOf(car.getType());
    }
}
