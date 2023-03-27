package com.example.jpa.model.baord;

import com.example.jpa.JpaDempApplication;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.tomcat.util.compat.JrePlatform;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.jpa.JpaDempApplication.lorem;
import static com.example.jpa.JpaDempApplication.random;

@Entity
@Table(name = "tbl_boards")
@EqualsAndHashCode(of = "bno")
@ToString(exclude = {"replies", "thumbnail"})
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;
    private String title;
    private String writer;
    private String content;

    @CreatedDate
    private LocalDateTime regdate;
    @UpdateTimestamp
    private LocalDateTime updatedate;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "bno")
    @OrderColumn()
    private List<Reply> replies;

    @OneToOne(mappedBy = "board", cascade = CascadeType.PERSIST)
    private Thumbnail thumbnail;

    public static Board random() {
        Board board = new Board();
        board.title = lorem.getTitle(1);
        board.writer = lorem.getName();
        board.content = lorem.getWords(10);
        board.thumbnail = new Thumbnail("https://picsum.photos/200/300", board);
        board.replies = new ArrayList<>();
        for (int i = 0; i < random.nextInt(3); i++) {
            board.replies.add(new Reply(lorem.getName(), lorem.getWords(3)));
        }
        return board;
    }
}
/*
create table tbl_boards
(
    bno        bigint not null auto_increment,
    content    varchar(255),
    regdate    datetime,
    title      varchar(255),
    updatedate datetime,
    writer     varchar(255),
    primary key (bno)
) engine = InnoDB;
*/