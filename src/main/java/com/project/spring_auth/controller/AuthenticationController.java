package com.project.spring_auth.controller;

import com.project.spring_auth.dto.LoginUser;
import com.project.spring_auth.dto.RegisterUser;
import com.project.spring_auth.dto.VerifyUser;
import com.project.spring_auth.model.User;
import com.project.spring_auth.responses.LoginResponse;
import com.project.spring_auth.service.AuthenticationService;
import com.project.spring_auth.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUser registerUser) {
        User user = authenticationService.signup( registerUser );
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUser loginUser) {
        User user = authenticationService.authenticate( loginUser );
        String jwtToken = jwtService.generateToken( user );
        LoginResponse loginResponse = new LoginResponse( jwtToken, jwtService.getExpirationTime() );
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUser verifyUser) {
        try {
            authenticationService.verifyUser( verifyUser );
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
           return ResponseEntity.badRequest().body( e.getMessage() );
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestBody String email) {
        try {
            authenticationService.resendVerificationCode( email );
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body( "Error Message" );
        }
    }

}
