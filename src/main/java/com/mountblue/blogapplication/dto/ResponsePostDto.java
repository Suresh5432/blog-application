package com.mountblue.blogapplication.dto;

import com.mountblue.blogapplication.entity.Comments;
import com.mountblue.blogapplication.entity.Users;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ResponsePostDto {
    private Long id;
    private String title;
    private String excerpt;
    private String content;
    private Users author;
    private LocalDateTime publishedAt;
    private boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String tags;
    private List<Comments> comments;

}
