package com.mountblue.blogapplication.service;

import com.mountblue.blogapplication.dto.RestCommentResponseDto;
import com.mountblue.blogapplication.entity.Comments;
import com.mountblue.blogapplication.entity.Post;
import com.mountblue.blogapplication.entity.Users;
import com.mountblue.blogapplication.repository.CommentsRepository;
import com.mountblue.blogapplication.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentsServiceImplement implements CommentsService {

    private final PostRepository postRepository;
    private final CommentsRepository commentsRepository;

    @Autowired
    public CommentsServiceImplement(PostRepository postRepository, CommentsRepository commentsRepository) {
        this.postRepository = postRepository;
        this.commentsRepository = commentsRepository;
    }
    @Transactional
    @Override
    public void addComment(Long id, Comments comments, Users user) {
        Post post =postRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Post not found"));
        comments.setPost(post);
        comments.setUser(user);
        post.addComments(comments);
        postRepository.save(post);
    }
    @Transactional
    @Override
    public void addComments(Long id, Comments comments) {
        Post post = postRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Post not found"));
        post.addComments(comments);
        postRepository.save(post);
    }


    @Override
    public Comments findCommentById(Long id) {
        return commentsRepository.findById(id).orElseThrow(()->new RuntimeException("comment not found"));
    }

    @Override
    public List<RestCommentResponseDto> findAllByPostId(Long postId) {

        List<Comments> comments =
                commentsRepository.findByPostId(postId);

        return comments.stream()
                .map(comment -> {

                    RestCommentResponseDto dto =
                            new RestCommentResponseDto();

                    dto.setId(comment.getId());
                    dto.setComment(comment.getComment());

                    if (comment.getPost() != null) {
                        dto.setPostId(comment.getPost().getId());
                    }

                    if (comment.getUser() != null) {
                        dto.setUserId(comment.getUser().getId());
                        dto.setUserName(comment.getUser().getName());
                    }

                    dto.setCreatedAt(comment.getCreatedAt());
                    dto.setUpdatedAt(comment.getUpdatedAt());

                    return dto;
                })
                .toList();
    }

    @Transactional
    @Override
    public void updateComment(Long commentId, String comments) {
        Comments comment=findCommentById(commentId);
        comment.setComment(comments);
        commentsRepository.save(comment);
    }
    @Transactional
    @Override
    public void updateComment(Long commentId, String comments, Users user) {
        Comments comment = findCommentById(commentId);

        if (comment.getUser() == null ||
                !comment.getUser().getId().equals(user.getId())) {

            throw new RuntimeException(
                    "You are not allowed to update this comment");
        }

        comment.setComment(comments);

        commentsRepository.save(comment);
    }


    @Override
    public void deleteComment(Long id) {
        Comments comment=findCommentById(id);
        commentsRepository.delete(comment);
    }

    @Override
    public void deleteComment(Long id, Users user) {
        Comments comment = findCommentById(id);
        if (comment.getUser() == null ||!comment.getUser().getId().equals(user.getId())){
                throw new RuntimeException("You are not allowed to delete this comment");
        }
        commentsRepository.delete(comment);
    }

    @Override
    public RestCommentResponseDto findCommentResponseById(Long id) {
        Comments comment = findCommentById(id);

        RestCommentResponseDto dto =
                new RestCommentResponseDto();

        dto.setId(comment.getId());
        dto.setComment(comment.getComment());

        if (comment.getPost() != null) {
            dto.setPostId(comment.getPost().getId());
        }

        if (comment.getUser() != null) {
            dto.setUserId(comment.getUser().getId());
            dto.setUserName(comment.getUser().getName());
        }

        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());

        return dto;
    }
}
