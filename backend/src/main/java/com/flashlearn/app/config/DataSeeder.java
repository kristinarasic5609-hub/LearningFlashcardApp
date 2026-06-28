package com.flashlearn.app.config;

import com.flashlearn.app.model.entity.Flashcard;
import com.flashlearn.app.model.entity.FlashcardSet;
import com.flashlearn.app.model.entity.User;
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
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@flashcard.app").isPresent()) {
                return;
            }

            User admin = new User();
            admin.setEmail("admin@flashcard.app");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);

            User user = new User();
            user.setEmail("user@flashcard.app");
            user.setUsername("demo_user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole("USER");
            userRepository.save(user);

            FlashcardSet sampleSet = new FlashcardSet();
            sampleSet.setTitle("JavaScript Basics");
            sampleSet.setDescription("Core JavaScript concepts for beginners");
            sampleSet.setCategory("Programming");
            sampleSet.setPublic(true);
            sampleSet.setOwnerId(user.getId());

            Flashcard card1 = new Flashcard();
            card1.setQuestion("What is a closure?");
            card1.setAnswer("A function with access to its outer scope");

            Flashcard card2 = new Flashcard();
            card2.setQuestion("What does === compare?");
            card2.setAnswer("Value and type");

            Flashcard card3 = new Flashcard();
            card3.setQuestion("What is hoisting?");
            card3.setAnswer("Moving declarations to the top of scope");

            sampleSet = flashcardSetRepository.save(sampleSet);

            card1.setFlashcardSetId(sampleSet.getId());
            card2.setFlashcardSetId(sampleSet.getId());
            card3.setFlashcardSetId(sampleSet.getId());

            sampleSet.getFlashcards().add(card1);
            sampleSet.getFlashcards().add(card2);
            sampleSet.getFlashcards().add(card3);

            flashcardSetRepository.save(sampleSet);
        };
    }
}
