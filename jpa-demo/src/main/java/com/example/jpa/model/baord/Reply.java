package com.example.jpa.model.baord;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tbl_replies")
@EqualsAndHashCode
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    private String replyText;
    private String replyer;

    @CreationTimestamp
    private LocalDateTime regdate;
    @UpdateTimestamp
    private LocalDateTime updatedate;

    public Reply(String replyer, String replyText) {
        this.replyText = replyText;
        this.replyer = replyer;
    }

    protected Reply() {
    }
}
/*
create table tbl_replies
(
    rno           bigint not null auto_increment,
    regdate       datetime,
    reply_text    varchar(255),
    replyer       varchar(255),
    updatedate    datetime,
    bno           bigint,
    replies_order integer,
    primary key (rno)
) engine = InnoDB
*/