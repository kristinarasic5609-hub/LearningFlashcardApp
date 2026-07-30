package com.flashlearn.app.controller;

import com.flashlearn.app.model.dto.AuthUserDto;
import com.flashlearn.app.model.dto.CreateFlashcardRequest;
import com.flashlearn.app.model.dto.UpdateFlashcardRequest;
import com.flashlearn.app.model.entity.Flashcard;
import com.flashlearn.app.security.SecurityUtils;
import com.flashlearn.app.service.FlashcardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FlashcardController {

    private final FlashcardService flashcardService;

    public FlashcardController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    @PostMapping("/sets/{id}/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public Flashcard addCard(
            @PathVariable("id") String setId,
            @Valid @RequestBody CreateFlashcardRequest request) {
        AuthUserDto user = SecurityUtils.requireCurrentUser();
        return flashcardService.createFlashcard(setId, user, request);
    }

    @PutMapping("/cards/{id}")
    public Flashcard updateCard(
            @PathVariable String id,
            @Valid @RequestBody UpdateFlashcardRequest request) {
        AuthUserDto user = SecurityUtils.requireCurrentUser();
        return flashcardService.updateFlashcard(id, user, request);
    }

    @DeleteMapping("/cards/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable String id) {
        AuthUserDto user = SecurityUtils.requireCurrentUser();
        flashcardService.deleteFlashcard(id, user);
    }
}
