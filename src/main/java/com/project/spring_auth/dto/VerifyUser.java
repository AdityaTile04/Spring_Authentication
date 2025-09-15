package com.project.spring_auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyUser {
    private String email;
    private String verificationCode;
}
