package com.flashlearn.app.factory;

import com.flashlearn.app.model.entity.Flashcard;
import org.springframework.stereotype.Component;

/**
 * Factory for creating Flashcard entities.
 * Centralizes object creation instead of scattering {@code new Flashcard()} across the codebase.
 */
@Component
public class FlashcardFactory {

    public Flashcard createFlashcard(String flashcardSetId, String question, String answer) {
        Flashcard card = new Flashcard();
        card.setFlashcardSetId(flashcardSetId);
        card.setQuestion(question);
        card.setAnswer(answer);
        return card;
    }
}
