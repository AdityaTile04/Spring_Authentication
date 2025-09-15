package com.project.spring_auth.service;

import com.project.spring_auth.dto.LoginUser;
import com.project.spring_auth.dto.RegisterUser;
import com.project.spring_auth.dto.VerifyUser;
import com.project.spring_auth.model.User;
import com.project.spring_auth.repository.UserRepo;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepo repo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(
            UserRepo repo,
            PasswordEncoder encoder,
            AuthenticationManager authenticationManager,
            EmailService emailService,
            PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUser input) {
        User user = new User(input.getUsername(), input.getEmail(), encoder.encode( input.getPassword() ));
        user.setVerificationCode( generateVerificationCode() );
        user.setVerificationExpireAt( LocalDateTime.now().plusMinutes( 15 ) );
        user.setEnabled( false );
        sendVerificationEmail(user);
        return repo.save(user);
    }

    public User authenticate(LoginUser input) {
        User user = repo.findByEmail( input.getEmail() ).orElseThrow(() -> new RuntimeException("User not found"));

        if(!user.isEnabled()) {
            throw new RuntimeException("Account not verified, please verify your account");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
                return user;
    }

    public void verifyUser(VerifyUser input) {
        Optional<User> optionalUser = repo.findByEmail( input.getEmail() );
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(user.getVerificationExpireAt().isBefore( LocalDateTime.now() )) {
                throw new RuntimeException("Verification Code has expired");
            }
            if(user.getVerificationCode().equals( input.getVerificationCode() )) {
                user.setEnabled( true );
                user.setVerificationCode( null );
                user.setVerificationExpireAt( null );
                repo.save( user );
            } else {
                throw new RuntimeException("Invalid Verification Code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = repo.findByEmail( email );

        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(user.isEnabled()) {
                throw new RuntimeException("Account Already Verify");
            }
            user.setVerificationCode( generateVerificationCode() );
            user.setVerificationExpireAt( LocalDateTime.now().plusHours( 1 ) );
            sendVerificationEmail(user);
            repo.save( user );
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void sendVerificationEmail(User user) {
        String subject = "Account Verification";
        String verificationCode = user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try{
            emailService.sendEmailVerification( user.getEmail(), subject,htmlMessage );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(9000000) + 1000000;
        return String.valueOf( code );
    }

}
