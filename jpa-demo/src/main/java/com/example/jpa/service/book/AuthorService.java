package com.example.jpa.service.book;

import com.example.jpa.model.book.Author;
import com.example.jpa.repository.book.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository repository;

    @Transactional(readOnly = true)
    public List<Author> findAll() {
        return repository.findAllWithPageable(PageRequest.of(0, 10));
    }
}
