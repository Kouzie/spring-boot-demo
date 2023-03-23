package com.example.redis.controller;

import com.example.redis.model.Point;
import com.example.redis.repository.PointRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/repository")
@RequiredArgsConstructor
public class RedisRepositoryController {

    private final PointRedisRepository repository;

    @GetMapping("/{id}")
    public Point getPointById(@PathVariable String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("invalid id:" + id));
    }

    @PostMapping
    public Point addPoint(@RequestBody Point point) {
        return repository.save(point);
    }

    @DeleteMapping("/{id}")
    public void removePoint(@PathVariable String id) {
        repository.deleteById(id);
    }

    @PatchMapping("/{id}")
    public Point updatePoint(@PathVariable String id, @RequestBody Point point) {
        Point entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("invalid id:" + id));
        entity.update(point);
        entity= repository.save(entity);
        return entity;
    }
}
