package com.example.mapper.mapper;


import com.example.mapper.modal.Car;
import com.example.mapper.request.CarDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {})
public interface CarMapper extends EntityMapper<CarDto, Car> {

    @Mapping(source = "seatCount", target = "numberOfSeats")
    @Mapping(source = "carId", target = "id", ignore = true)
    Car toEntity(CarDto carDto);

    @Mapping(source = "id", target = "carId")
    @Mapping(source = "numberOfSeats", target = "seatCount")
    CarDto toDto(Car car);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(source = "carId", target = "id", ignore = true)
//    @Mapping(source = "seatCount", target = "numberOfSeats")
    void partialUpdate(@MappingTarget Car entity, CarDto dto);

}
