package com.flashlearn.app.model.dto;

import java.time.Instant;
import java.util.List;

public record UserStatisticsResponse(
        String userId,
        int totalCardsStudied,
        int correctAnswers,
        int incorrectAnswers,
        int successPercentage,
        List<ProgressHistoryEntry> progressHistory
) {
    public record ProgressHistoryEntry(
            String sessionId,
            String flashcardSetId,
            String flashcardSetTitle,
            Instant studiedAt,
            int cardsStudied,
            int correctAnswers,
            int incorrectAnswers,
            int successPercentage
    ) {
    }
}
