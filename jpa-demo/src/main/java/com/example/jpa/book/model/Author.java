package com.example.jpa.book.model;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tbl_author")
@EqualsAndHashCode(of = "ano")
@ToString(exclude = {"books"})
@NoArgsConstructor
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ano;
    private String name;
    private LocalDate birth;

    @OneToMany(mappedBy = "author")
    private List<Book> books;

    public Author(String name) {
        this.name = name;
        this.birth = LocalDate.now();
    }
}