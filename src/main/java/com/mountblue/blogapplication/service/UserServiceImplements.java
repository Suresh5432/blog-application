package com.mountblue.blogapplication.service;

import com.mountblue.blogapplication.dto.RequestUsersDto;
import com.mountblue.blogapplication.dto.ResponseUsersDto;
import com.mountblue.blogapplication.entity.Users;
import com.mountblue.blogapplication.enums.Role;
import com.mountblue.blogapplication.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
@RequiredArgsConstructor
@Service
public class UserServiceImplements implements UserService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public Users toUser(RequestUsersDto requestUsersDto) {
        Users user=new Users();
        user.setName(requestUsersDto.getName());
        user.setEmail(requestUsersDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestUsersDto.getPassword()));
        user.setRole(Role.AUTHOR);
        return user;
    }
    public ResponseUsersDto toResponseUser(Users user) {
        ResponseUsersDto responseUsersDto=new ResponseUsersDto();
        responseUsersDto.setId(user.getId());
        responseUsersDto.setEmail(user.getEmail());
        responseUsersDto.setName(user.getName());
        return responseUsersDto;
    }
    @Transactional
    @Override
    public void saveUser(RequestUsersDto user) {
        usersRepository.save(toUser(user));
    }

    @Override
    public ResponseUsersDto findByEmailAndPassword(String email, String password) {
        return usersRepository.findByEmailAndPassword(email,password)
                .map(this::toResponseUser).orElse(null);
    }
}
