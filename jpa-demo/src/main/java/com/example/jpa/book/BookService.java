package com.example.jpa.book;

import com.example.jpa.book.model.Author;
import com.example.jpa.book.model.AuthorRepository;
import com.example.jpa.book.model.Book;
import com.example.jpa.book.model.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.List;

import static com.example.jpa.JpaDempApplication.lorem;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository repository;
    private final AuthorRepository authorRepository;

    @PostConstruct
    private void init() {
        for (int i = 0; i < 5; i++) {
            Author author = authorRepository.save(new Author(lorem.getName()));
            repository.save(new Book(lorem.getTitle(1), author));
            repository.save(new Book(lorem.getTitle(1), author));
            repository.save(new Book(lorem.getTitle(1), author));

        }
    }

    @Transactional
    public Book save(Book Book) {
        log.info("save invoked");
        return repository.save(Book);
    }

    @Transactional
    public Book findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Book, id:" + id));
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Book> findAll() {
//         return repository.findAll();
        Page<Book> pageResult = repository.findAllWithPageableResult(PageRequest.of(0, 10));
        return pageResult.getContent();
    }
}
