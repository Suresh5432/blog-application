package com.mountblue.blogapplication.service;

import com.mountblue.blogapplication.dto.RequestUsersDto;
import com.mountblue.blogapplication.dto.ResponseUsersDto;
import com.mountblue.blogapplication.entity.Users;
import org.apache.catalina.User;

public interface UserService {
    void saveUser(RequestUsersDto user);
    ResponseUsersDto findByEmailAndPassword(String email,String password);
}
