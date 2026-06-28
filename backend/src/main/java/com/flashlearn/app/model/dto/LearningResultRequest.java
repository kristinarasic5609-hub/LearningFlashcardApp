package com.flashlearn.app.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LearningResultRequest {

    @NotBlank(message = "Valid session ID is required")
    private String sessionId;

    @NotBlank(message = "Valid flashcard ID is required")
    private String flashcardId;

    @NotNull(message = "known must be a boolean")
    private Boolean known;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getFlashcardId() {
        return flashcardId;
    }

    public void setFlashcardId(String flashcardId) {
        this.flashcardId = flashcardId;
    }

    public Boolean getKnown() {
        return known;
    }

    public void setKnown(Boolean known) {
        this.known = known;
    }
}
