package com.mountblue.blogapplication.repository;

import com.mountblue.blogapplication.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentsRepository extends JpaRepository<Comments, Long> {
}
