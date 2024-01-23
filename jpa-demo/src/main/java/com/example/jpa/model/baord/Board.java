package com.example.jpa.model.baord;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
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

    @CreationTimestamp
    private LocalDateTime regdate;
    @UpdateTimestamp
    private LocalDateTime updatedate;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "bno")
    private List<Reply> replies;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    @JoinColumn(name = "bno")
    private List<Attachment> attachments; // 첨부파일

    @OneToOne(mappedBy = "board", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Thumbnail thumbnail;

    @Version
    private Long version;

    public static Board random() {
        Board board = new Board();
        board.title = lorem.getTitle(1);
        board.writer = lorem.getName();
        board.content = lorem.getWords(10);
//        board.thumbnail = new Thumbnail("https://picsum.photos/200/300", board);
        board.replies = new ArrayList<>();
        for (int i = 0; i < random.nextInt(3) + 1; i++) {
            board.replies.add(new Reply(lorem.getName(), lorem.getWords(3)));
        }
        board.attachments = new ArrayList<>();
        for (int i = 0; i < random.nextInt(3) + 1; i++) {
            board.attachments.add(new Attachment("https://picsum.photos/200/300"));
        }
        return board;
    }

    @JsonIgnore
    public void testOrphan() {
        this.replies.clear();
        this.attachments.clear();
    }

    @JsonIgnore
    public void update() {
        this.title = lorem.getTitle(1);
        this.content = lorem.getWords(10);
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