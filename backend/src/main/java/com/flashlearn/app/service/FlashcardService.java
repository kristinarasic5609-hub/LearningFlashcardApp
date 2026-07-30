package com.flashlearn.app.service;

import com.flashlearn.app.exception.AppException;
import com.flashlearn.app.factory.FlashcardFactory;
import com.flashlearn.app.model.dto.AuthUserDto;
import com.flashlearn.app.model.dto.CreateFlashcardRequest;
import com.flashlearn.app.model.dto.UpdateFlashcardRequest;
import com.flashlearn.app.model.entity.Flashcard;
import com.flashlearn.app.model.entity.FlashcardSet;
import com.flashlearn.app.repository.FlashcardRepository;
import com.flashlearn.app.repository.FlashcardSetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final FlashcardSetRepository flashcardSetRepository;
    private final FlashcardFactory flashcardFactory;

    public FlashcardService(
            FlashcardRepository flashcardRepository,
            FlashcardSetRepository flashcardSetRepository,
            FlashcardFactory flashcardFactory) {
        this.flashcardRepository = flashcardRepository;
        this.flashcardSetRepository = flashcardSetRepository;
        this.flashcardFactory = flashcardFactory;
    }

    @Transactional
    public Flashcard createFlashcard(String setId, AuthUserDto user, CreateFlashcardRequest request) {
        ensureOwnership(setId, user.id());

        Flashcard card = flashcardFactory.createFlashcard(
                setId, request.getQuestion(), request.getAnswer());
        return flashcardRepository.save(card);
    }

    @Transactional
    public Flashcard updateFlashcard(String cardId, AuthUserDto user, UpdateFlashcardRequest request) {
        Flashcard card = flashcardRepository.findByIdWithSet(cardId)
                .orElseThrow(() -> new AppException(404, "Flashcard not found"));

        if (!card.getFlashcardSet().getOwnerId().equals(user.id())) {
            throw new AppException(403, "You can only modify flashcards in your own sets");
        }

        if (request.getQuestion() != null) {
            card.setQuestion(request.getQuestion());
        }
        if (request.getAnswer() != null) {
            card.setAnswer(request.getAnswer());
        }

        return flashcardRepository.save(card);
    }

    @Transactional
    public void deleteFlashcard(String cardId, AuthUserDto user) {
        Flashcard card = flashcardRepository.findByIdWithSet(cardId)
                .orElseThrow(() -> new AppException(404, "Flashcard not found"));

        if (!card.getFlashcardSet().getOwnerId().equals(user.id())) {
            throw new AppException(403, "You can only delete flashcards from your own sets");
        }

        flashcardRepository.delete(card);
    }

    private void ensureOwnership(String setId, String ownerId) {
        FlashcardSet set = flashcardSetRepository.findByIdWithDetails(setId)
                .orElseThrow(() -> new AppException(404, "Flashcard set not found"));

        if (!set.getOwnerId().equals(ownerId)) {
            throw new AppException(403, "You can only add flashcards to your own sets");
        }
    }
}
