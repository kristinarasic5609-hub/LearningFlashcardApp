package com.flashlearn.app.controller;

import com.flashlearn.app.model.dto.AuthUserDto;
import com.flashlearn.app.model.dto.CreateFlashcardSetRequest;
import com.flashlearn.app.model.dto.UpdateFlashcardSetRequest;
import com.flashlearn.app.model.entity.FlashcardSet;
import com.flashlearn.app.security.SecurityUtils;
import com.flashlearn.app.service.FlashcardSetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sets")
public class FlashcardSetController {

    private final FlashcardSetService flashcardSetService;

    public FlashcardSetController(FlashcardSetService flashcardSetService) {
        this.flashcardSetService = flashcardSetService;
    }

    @GetMapping
    public List<FlashcardSet> listSets(@RequestParam(required = false) String search) {
        return flashcardSetService.listPublic(search);
    }

    @GetMapping("/mine")
    public List<FlashcardSet> listMySets() {
        AuthUserDto user = SecurityUtils.requireCurrentUser();
        return flashcardSetService.listByOwner(user.id());
    }

    @GetMapping("/{id}")
    public FlashcardSet getSet(@PathVariable String id) {
        AuthUserDto user = SecurityUtils.getCurrentUser();
        return flashcardSetService.getById(id, user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FlashcardSet createSet(@Valid @RequestBody CreateFlashcardSetRequest request) {
        AuthUserDto user = SecurityUtils.requireCurrentUser();
        return flashcardSetService.create(user.id(), request);
    }

    @PutMapping("/{id}")
    public FlashcardSet updateSet(
            @PathVariable String id,
            @Valid @RequestBody UpdateFlashcardSetRequest request) {
        AuthUserDto user = SecurityUtils.requireCurrentUser();
        return flashcardSetService.update(id, user, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSet(@PathVariable String id) {
        AuthUserDto user = SecurityUtils.requireCurrentUser();
        flashcardSetService.delete(id, user);
    }
}
