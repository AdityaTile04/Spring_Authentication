package com.project.spring_auth.service;

import com.project.spring_auth.model.User;
import com.project.spring_auth.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo, EmailService service) {
        this.userRepo = userRepo;
    }

    public List<User> allUser() {
        List<User> users = new ArrayList<>();
        userRepo.findAll().forEach( users::add );
        return users;
    }
}
