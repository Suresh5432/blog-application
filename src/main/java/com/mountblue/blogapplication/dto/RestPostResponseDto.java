package com.mountblue.blogapplication.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RestPostResponseDto {
    private Long id;
    private String title;
    private String excerpt;
    private String content;
    private AuthorDto author;
    private LocalDateTime publishedAt;
    private boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String tags;
}
