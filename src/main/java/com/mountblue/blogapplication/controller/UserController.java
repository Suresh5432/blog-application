package com.mountblue.blogapplication.controller;

import com.mountblue.blogapplication.dto.RequestUsersDto;
import com.mountblue.blogapplication.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    UserService userService;
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/register")
    public String registerUser(Model model) {
        model.addAttribute("user",new RequestUsersDto());
        return "user-register";
    }
    @PostMapping("/register")
    public String saveUser(@Valid @ModelAttribute("user") RequestUsersDto user,
                           BindingResult result) {
        if (result.hasErrors()) {
            return "user-register";
        }
        userService.saveUser(user);
        return "redirect:/login";
    }
    @GetMapping("/login")
    public String login() {
        return "user-login";
    }
    @GetMapping("/logout")
    public String logout() {
        return "user-logout";
    }

}
