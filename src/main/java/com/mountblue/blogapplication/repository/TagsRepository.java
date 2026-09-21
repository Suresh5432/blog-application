package com.mountblue.blogapplication.repository;

import com.mountblue.blogapplication.entity.Tags;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagsRepository extends JpaRepository<Tags, Long> {
    Optional<Tags> findByName(String name);
    List<Tags> findAllByOrderByIdAsc();
}
