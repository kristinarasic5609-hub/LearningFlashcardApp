package com.flashlearn.app;

import com.flashlearn.app.factory.FlashcardFactory;
import com.flashlearn.app.model.entity.Flashcard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlashcardCreationTest {

    private final FlashcardFactory flashcardFactory = new FlashcardFactory();

    @Test
    void factoryCreatesFlashcardWithFields() {
        Flashcard card = flashcardFactory.createFlashcard(
                "set-123", "What is JPA?", "Java Persistence API");

        assertNotNull(card);
        assertEquals("set-123", card.getFlashcardSetId());
        assertEquals("What is JPA?", card.getQuestion());
        assertEquals("Java Persistence API", card.getAnswer());
    }

    @Test
    void factoryCreatesDistinctInstances() {
        Flashcard first = flashcardFactory.createFlashcard("set-1", "Q1", "A1");
        Flashcard second = flashcardFactory.createFlashcard("set-1", "Q2", "A2");

        assertNotSame(first, second);
        assertNotEquals(first.getQuestion(), second.getQuestion());
    }
}
