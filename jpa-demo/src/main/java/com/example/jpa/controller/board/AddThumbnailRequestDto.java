package com.example.jpa.controller.board;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AddThumbnailRequestDto {
    @NotNull
    private Long boardId;
}
