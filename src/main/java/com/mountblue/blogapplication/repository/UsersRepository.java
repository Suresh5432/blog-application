package com.mountblue.blogapplication.repository;

import com.mountblue.blogapplication.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmailAndPassword(String email,String password);
    Optional<Users> findByName(String name);
    Optional<Users> findByEmail(String email);
}
