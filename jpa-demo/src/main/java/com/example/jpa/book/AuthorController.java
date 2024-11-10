package com.example.jpa.book;

import com.example.jpa.book.model.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService service;

    @GetMapping
    public List<Author> getAllBooks() {
        return service.findAll();
    }
}
