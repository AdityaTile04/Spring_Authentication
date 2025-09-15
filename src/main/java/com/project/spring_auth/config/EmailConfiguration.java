package com.project.spring_auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfiguration {
    @Value( "${spring.mail.username}" )
    private String emailUsername;
    @Value("${spring.mail.password}")
    private String password;

    @Bean
   public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost( "smtp.gmail.com" );
        mailSender.setPort( 587 );
        mailSender.setUsername( emailUsername );
        mailSender.setPassword( password );

        Properties pros = mailSender.getJavaMailProperties();
        pros.put( "mail.transport.protocol" , "smtp");
        pros.put( "mail.smtp.auth", "true" );
        pros.put("mail.smtp.starttls.enable", "true");
        pros.put( "mail.debug", "true" );

        return mailSender;

    }
}
