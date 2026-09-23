package com.mountblue.blogapplication.service;

import com.mountblue.blogapplication.dto.RequestPostDto;
import com.mountblue.blogapplication.dto.ResponsePostDto;
import com.mountblue.blogapplication.dto.RestPostResponseDto;
import com.mountblue.blogapplication.entity.Comments;
import com.mountblue.blogapplication.entity.Post;
import com.mountblue.blogapplication.entity.Tags;
import com.mountblue.blogapplication.entity.Users;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PostService {
    List<Users> findAllAuthors();
    Post findById(Long id);
    RequestPostDto findPostById(Long id);
    Page<ResponsePostDto> findPost(Integer start,
                                   Integer limit,
                                   Long authorId,
                                   List<Long> tagIds,
                                   LocalDate publishedFrom,
                                   LocalDate publishedTo,
                                   String sortField,
                                   String order, String search);
    List<Tags> findAllTags();
    void deletePost(Long id);
    Post savePost(RequestPostDto post,String tagName,Users userDetails);

    //rest services

    Page<RestPostResponseDto> findRestPosts(
            Integer start,
            Integer limit,
            Long authorId,
            List<Long> tagIds,
            LocalDate publishedFrom,
            LocalDate publishedTo,
            String sortField,
            String order,
            String search
    );

    RestPostResponseDto findRestPostById(Long id);

}
