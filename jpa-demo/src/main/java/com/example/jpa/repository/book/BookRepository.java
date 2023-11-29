package com.example.jpa.repository.book;

import com.example.jpa.model.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value = "SELECT b FROM Book b LEFT JOIN FETCH b.author",
            countQuery = "SELECT COUNT(b.bno) FROM Book b")
    Page<Book> findAllWithPageableResult(Pageable pageable);
}
