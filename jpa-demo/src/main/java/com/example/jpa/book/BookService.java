package com.example.jpa.book;

import com.example.jpa.book.model.Author;
import com.example.jpa.book.model.AuthorRepository;
import com.example.jpa.book.model.Book;
import com.example.jpa.book.model.BookRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.jpa.JpaDempApplication.lorem;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository repository;
    private final AuthorRepository authorRepository;
    private final EntityManager entityManager;

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
        Page<Book> pageResult = repository.findAllWithPageableResult(PageRequest.of(0, 10));
        return pageResult.getContent();
    }

    @Transactional(readOnly = true)
    public List<Book> search(String title, String authorName, LocalDate createdFrom, LocalDate createdTo) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> book = query.from(Book.class);
        Join<Book, Author> author = book.join("author", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        // 제목 검색 (LIKE 검색)
        if (title != null && !title.isEmpty()) {
            predicates.add(cb.like(cb.lower(book.get("title")), "%" + title.toLowerCase() + "%"));
        }

        // 작성자 이름 검색 (LIKE 검색)
        if (authorName != null && !authorName.isEmpty()) {
            predicates.add(cb.like(cb.lower(author.get("name")), "%" + authorName.toLowerCase() + "%"));
        }

        // 생성일 시작 범위
        if (createdFrom != null) {
            predicates.add(cb.greaterThanOrEqualTo(book.get("created"), createdFrom));
        }

        // 생성일 종료 범위
        if (createdTo != null) {
            predicates.add(cb.lessThanOrEqualTo(book.get("created"), createdTo));
        }

        // 조건 조합
        query.where(predicates.toArray(new Predicate[0]));

        // 정렬 (생성일 내림차순)
        query.orderBy(cb.desc(book.get("created")));

        TypedQuery<Book> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }
}
