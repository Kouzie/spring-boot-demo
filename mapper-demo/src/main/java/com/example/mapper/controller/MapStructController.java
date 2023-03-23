package com.example.mapper.controller;

import com.example.mapper.mapper.CarMapper;
import com.example.mapper.modal.Car;
import com.example.mapper.repository.CarRepository;
import com.example.mapper.request.CarDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/car")
public class MapStructController {

    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @GetMapping("/{id}")
    public CarDto getCar(@PathVariable Long id) {
        Car car = carRepository.findById(id);
        return carMapper.toDto(car);
    }

    @PostMapping
    CarDto setCar(@RequestBody CarDto carDto) {
        Car car = carMapper.toEntity(carDto);
        carRepository.save(car);
        return carMapper.toDto(car);
    }

    @PatchMapping("{id}")
    CarDto patchCar(@PathVariable Long id, @RequestBody CarDto carDto) {
        Car car = carRepository.findById(id);
        carMapper.partialUpdate(car, carDto);
        car = carRepository.save(car);
        return carMapper.toDto(car);
    }

}
