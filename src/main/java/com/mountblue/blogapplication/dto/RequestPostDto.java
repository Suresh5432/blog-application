package com.mountblue.blogapplication.dto;

import com.mountblue.blogapplication.entity.Comments;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RequestPostDto {
    private Long id;
    @NotBlank(message = "Title is required")
    @Size(max = 150,message = "Title must not exceed 150 characters")
    private String title;
    private String excerpt;
    @NotBlank(message = "Content is required")
    @Size(max = 20_000, message = "Content must not exceed 20,000 characters")
    private String content;
    private String author;
    private LocalDateTime publishedAt;
    private boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @NotBlank(message = "Create at least one tag")
    private String tags;
    private List<Comments> comments;
}
