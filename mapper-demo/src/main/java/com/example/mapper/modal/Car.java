package com.example.mapper.modal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Car {
    private Long id;
    private String make;
    private Integer numberOfSeats;
    private CarType type;
}
