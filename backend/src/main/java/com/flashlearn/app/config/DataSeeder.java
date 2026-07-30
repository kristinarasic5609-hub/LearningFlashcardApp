package com.flashlearn.app.config;

import com.flashlearn.app.factory.FlashcardFactory;
import com.flashlearn.app.model.entity.Flashcard;
import com.flashlearn.app.model.entity.FlashcardSet;
import com.flashlearn.app.model.entity.User;
import com.flashlearn.app.repository.FlashcardRepository;
import com.flashlearn.app.repository.FlashcardSetRepository;
import com.flashlearn.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("!test")
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(
            UserRepository userRepository,
            FlashcardSetRepository flashcardSetRepository,
            FlashcardRepository flashcardRepository,
            FlashcardFactory flashcardFactory,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("user@flashcard.app").isPresent()) {
                return;
            }

            User user = new User();
            user.setEmail("user@flashcard.app");
            user.setUsername("demo_user");
            user.setPassword(passwordEncoder.encode("user123"));
            userRepository.save(user);

            FlashcardSet sampleSet = new FlashcardSet();
            sampleSet.setTitle("JavaScript Basics");
            sampleSet.setDescription("Core JavaScript concepts for beginners");
            sampleSet.setCategory("Programming");
            sampleSet.setPublic(true);
            sampleSet.setOwnerId(user.getId());
            sampleSet = flashcardSetRepository.save(sampleSet);

            Flashcard card1 = flashcardFactory.createFlashcard(
                    sampleSet.getId(), "What is a closure?", "A function with access to its outer scope");
            Flashcard card2 = flashcardFactory.createFlashcard(
                    sampleSet.getId(), "What does === compare?", "Value and type");
            Flashcard card3 = flashcardFactory.createFlashcard(
                    sampleSet.getId(), "What is hoisting?", "Moving declarations to the top of scope");

            flashcardRepository.save(card1);
            flashcardRepository.save(card2);
            flashcardRepository.save(card3);
        };
    }
}
