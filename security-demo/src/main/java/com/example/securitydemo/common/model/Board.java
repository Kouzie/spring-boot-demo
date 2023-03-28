package com.example.securitydemo.common.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.securitydemo.SecurityDemoApplication.lorem;
import static com.example.securitydemo.SecurityDemoApplication.random;

@Getter
@Entity
@Table(name = "tbl_boards")
@EqualsAndHashCode(of = "bno")
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

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "bno")
    private List<Reply> replies;


    public static Board random() {
        Board board = new Board();
        board.title = lorem.getTitle(1);
        board.writer = lorem.getName();
        board.content = lorem.getWords(10);
        board.replies = new ArrayList<>();
        for (int i = 0; i < random.nextInt(3) + 1; i++) {
            board.replies.add(new Reply(lorem.getName(), lorem.getWords(3)));
        }
        return board;
    }

}