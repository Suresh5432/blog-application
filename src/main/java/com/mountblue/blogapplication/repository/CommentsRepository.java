package com.mountblue.blogapplication.repository;

import com.mountblue.blogapplication.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Long> {
    List<Comments> findByPostId(Long postId);
}
