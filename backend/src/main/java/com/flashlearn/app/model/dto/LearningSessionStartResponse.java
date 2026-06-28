package com.flashlearn.app.model.dto;

import java.util.List;

public record LearningSessionStartResponse(
        String sessionId,
        String flashcardSetId,
        List<FlashcardStudyDto> flashcards
) {
}
