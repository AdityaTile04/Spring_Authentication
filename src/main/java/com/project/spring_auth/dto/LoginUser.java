package com.project.spring_auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUser {
    private String email;
    private String password;
}
