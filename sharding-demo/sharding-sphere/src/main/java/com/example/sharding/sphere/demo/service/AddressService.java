package com.example.sharding.sphere.demo.service;

import com.example.sharding.sphere.demo.RandomTestUtil;
import com.example.sharding.sphere.demo.dto.AddressDto;
import com.example.sharding.sphere.demo.repository.AddressEntity;
import com.example.sharding.sphere.demo.repository.AddressRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    @PostConstruct
    private void init() {
        for (int i = 0; i < 10; i++) {
            AddressEntity addressEntity = new AddressEntity(RandomTestUtil.generateRandomString(10));
            addressRepository.save(addressEntity);
        }
    }

    @Transactional(readOnly = true)
    public List<AddressDto> getAllAddress() {
        return addressRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    private AddressDto toDto(AddressEntity entity) {
        AddressDto dto = new AddressDto();
        dto.setAddressId(entity.getAddressId());
        dto.setAddressName(entity.getAddressName());
        return dto;
    }

    @Transactional
    public AddressDto addRandomAccount() {
        AddressEntity entity = new AddressEntity(RandomTestUtil.generateRandomString(10));
        entity = addressRepository.save(entity);
        return toDto(entity);
    }
}
