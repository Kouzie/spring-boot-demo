package com.example.jpa.board.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_thumbnails")
@Setter
public class Thumbnail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tno;
    private String path;

    @CreationTimestamp
    private LocalDateTime regdate;

    @UpdateTimestamp
    private LocalDateTime updatedate;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "my_bno")
    private Board board;

    public Thumbnail(String path, Board board) {
        this.path = path;
        this.board = board;
    }

    protected Thumbnail() {

    }
}
/*
create table tbl_thumbnails
(
    tno        bigint not null auto_increment,
    path       varchar(255),
    regdate    datetime,
    updatedate datetime,
    board_bno  bigint,
    primary key (tno)
) engine = InnoDB
*/