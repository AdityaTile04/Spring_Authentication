package com.project.spring_auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUser {
    private String email;
    private String password;
    private String username;
}
