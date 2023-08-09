package com.example.jpa.controller.board;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AddThumbnailRequestDto {
    @NotNull
    private Long boardId;
}
