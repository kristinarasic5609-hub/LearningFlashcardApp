package com.flashlearn.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class LearningFlashcardApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningFlashcardApplication.class, args);
    }
}
