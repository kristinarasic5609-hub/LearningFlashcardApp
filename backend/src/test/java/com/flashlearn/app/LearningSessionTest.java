package com.flashlearn.app;

import com.flashlearn.app.exception.AppException;
import com.flashlearn.app.model.dto.RegisterRequest;
import com.flashlearn.app.model.entity.FlashcardSet;
import com.flashlearn.app.model.entity.User;
import com.flashlearn.app.repository.*;
import com.flashlearn.app.service.LearningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LearningSessionTest {

    @Autowired
    private LearningService learningService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlashcardSetRepository flashcardSetRepository;

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private LearningResultRepository learningResultRepository;

    @Autowired
    private LearningSessionRepository learningSessionRepository;

    @Autowired
    private FlashcardRepository flashcardRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        learningResultRepository.deleteAll();
        learningSessionRepository.deleteAll();
        statisticsRepository.deleteAll();
        flashcardRepository.deleteAll();
        flashcardSetRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void startLearningRejectsEmptySet() {
        User user = new User();
        user.setEmail("learner@test.com");
        user.setUsername("learner");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);

        FlashcardSet emptySet = new FlashcardSet();
emptySet.setTitle("Empty");
emptySet.setDescription("No cards");
emptySet.setCategory("Test");
emptySet.setOwnerId(user.getId());

FlashcardSet savedSet = flashcardSetRepository.save(emptySet);

AppException ex = assertThrows(
        AppException.class,
        () -> learningService.startLearning(user.getId(), savedSet.getId())
);

assertEquals(400, ex.getStatusCode());
    }
}
