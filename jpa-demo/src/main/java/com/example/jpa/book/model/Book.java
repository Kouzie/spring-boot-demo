package com.example.jpa.book.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tbl_book")
@EqualsAndHashCode(of = "bno")
@ToString(exclude = {"author"})
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;
    private String title;
    private LocalDate created;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private Author author;

    public Book(String title, Author author) {
        this.title = title;
        this.created = LocalDate.now();
        this.author = author;
    }
}