package com.edu.ulab.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class UserEntity {
    private Long id;
    private String fullName;
    private String title;
    private int age;
    private List<Long> bookList;
}
