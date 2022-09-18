package com.edu.ulab.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BookEntity {
    private Long id;
    private Long userId;
    private String title;
    private String author;
    private long pageCount;
    private UserEntity user;
}
