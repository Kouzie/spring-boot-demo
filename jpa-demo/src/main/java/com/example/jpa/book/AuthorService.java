package com.example.jpa.book;

import com.example.jpa.book.model.Author;
import com.example.jpa.book.model.AuthorRepository;
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
