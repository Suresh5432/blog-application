package com.mountblue.blogapplication.service;

import com.mountblue.blogapplication.dto.RestCommentResponseDto;
import com.mountblue.blogapplication.entity.Comments;
import com.mountblue.blogapplication.entity.Users;

import java.util.List;

public interface CommentsService {

    void addComment(Long id, Comments comments, Users user);

    void addComments(Long id,Comments comments);

    Comments findCommentById(Long id);

    List<RestCommentResponseDto> findAllByPostId(Long postId);

    void updateComment(Long commentId,String comments);

    void updateComment(Long commentId,String comments,Users user);

    void deleteComment(Long id);

    void deleteComment(Long id, Users user);

    RestCommentResponseDto findCommentResponseById(Long id);
}
