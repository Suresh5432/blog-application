package com.mountblue.blogapplication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorDto {
    private Long id;
    private String name;
    private String email;

    public AuthorDto(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
