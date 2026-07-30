package com.flashlearn.app.service;

import com.flashlearn.app.exception.AppException;
import com.flashlearn.app.model.dto.*;
import com.flashlearn.app.model.entity.Flashcard;
import com.flashlearn.app.model.entity.FlashcardSet;
import com.flashlearn.app.model.entity.LearningResult;
import com.flashlearn.app.model.entity.LearningSession;
import com.flashlearn.app.model.entity.Statistics;
import com.flashlearn.app.repository.FlashcardRepository;
import com.flashlearn.app.repository.FlashcardSetRepository;
import com.flashlearn.app.repository.LearningResultRepository;
import com.flashlearn.app.repository.LearningSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LearningService {

    private final FlashcardSetRepository flashcardSetRepository;
    private final FlashcardRepository flashcardRepository;
    private final LearningSessionRepository learningSessionRepository;
    private final LearningResultRepository learningResultRepository;
    private final StatisticsTrackingService statisticsTrackingService;

    public LearningService(
            FlashcardSetRepository flashcardSetRepository,
            FlashcardRepository flashcardRepository,
            LearningSessionRepository learningSessionRepository,
            LearningResultRepository learningResultRepository,
            StatisticsTrackingService statisticsTrackingService) {
        this.flashcardSetRepository = flashcardSetRepository;
        this.flashcardRepository = flashcardRepository;
        this.learningSessionRepository = learningSessionRepository;
        this.learningResultRepository = learningResultRepository;
        this.statisticsTrackingService = statisticsTrackingService;
    }

    @Transactional
    public LearningSessionStartResponse startLearning(String userId, String setId) {
        FlashcardSet set = flashcardSetRepository.findByIdWithDetails(setId)
                .orElseThrow(() -> new AppException(404, "Flashcard set not found"));

        if (set.getFlashcards().isEmpty()) {
            throw new AppException(400, "Cannot start learning session with an empty set");
        }

        LearningSession session = new LearningSession();
        session.setUserId(userId);
        session.setFlashcardSetId(setId);
        session = learningSessionRepository.save(session);

        List<FlashcardStudyDto> cards = set.getFlashcards().stream()
                .sorted(Comparator.comparing(Flashcard::getId))
                .map(card -> new FlashcardStudyDto(card.getId(), card.getQuestion(), card.getAnswer()))
                .toList();

        return new LearningSessionStartResponse(session.getId(), session.getFlashcardSetId(), cards);
    }

    @Transactional
    public LearningResult saveResults(AuthUserDto user, LearningResultRequest request) {
        LearningSession session = learningSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new AppException(404, "Learning session not found"));

        if (!session.getUserId().equals(user.id())) {
            throw new AppException(403, "Access denied");
        }

        if (!flashcardRepository.existsByIdAndFlashcardSetId(
                request.getFlashcardId(), session.getFlashcardSetId())) {
            throw new AppException(400, "Flashcard does not belong to this session");
        }

        LearningResult result = learningResultRepository
                .findBySessionIdAndFlashcardId(request.getSessionId(), request.getFlashcardId())
                .orElseGet(LearningResult::new);

        result.setSessionId(request.getSessionId());
        result.setFlashcardId(request.getFlashcardId());
        result.setKnown(Boolean.TRUE.equals(request.getKnown()));
        result.setAnsweredAt(Instant.now());
        result = learningResultRepository.save(result);

        long answeredCount = learningResultRepository.countBySessionId(request.getSessionId());
        long totalCards = flashcardRepository.countByFlashcardSetId(session.getFlashcardSetId());
        if (answeredCount >= totalCards && session.getCompletedAt() == null) {
            List<LearningResult> sessionResults =
                    learningResultRepository.findBySessionId(session.getId());
            int sessionCorrect = (int) sessionResults.stream().filter(LearningResult::isKnown).count();
            session.setScore(StatisticsTrackingService.calculateSessionScore(
                    sessionCorrect, sessionResults.size()));
            session.setCompletedAt(Instant.now());
            learningSessionRepository.save(session);
            statisticsTrackingService.updateUserStatistics(user.id());
        }

        return result;
    }

    @Transactional(readOnly = true)
    public UserStatisticsResponse getUserStatistics(String userId, AuthUserDto requester) {
        if (!requester.id().equals(userId)) {
            throw new AppException(403, "Access denied");
        }

        Statistics statistics = statisticsTrackingService.getUserStatistics(userId);
        List<LearningSession> sessions = learningSessionRepository.findByUserIdWithDetails(userId);
        List<LearningResult> allResults = learningResultRepository.findAllByUserId(userId);

        Map<String, List<LearningResult>> resultsBySession = allResults.stream()
                .collect(Collectors.groupingBy(LearningResult::getSessionId));

        int correctAnswers = statistics != null
                ? statistics.getCorrectAnswers()
                : (int) allResults.stream().filter(LearningResult::isKnown).count();
        int incorrectAnswers = statistics != null
                ? statistics.getWrongAnswers()
                : allResults.size() - correctAnswers;
        int totalCardsStudied = correctAnswers + incorrectAnswers;
        int successPercentage = statistics != null
                ? statistics.getSuccessRate()
                : (totalCardsStudied > 0
                        ? Math.round((correctAnswers * 100f) / totalCardsStudied)
                        : 0);

        List<UserStatisticsResponse.ProgressHistoryEntry> progressHistory = new ArrayList<>();
        for (LearningSession session : sessions) {
            List<LearningResult> sessionResults = resultsBySession.getOrDefault(session.getId(), List.of());
            int sessionCorrect = (int) sessionResults.stream().filter(LearningResult::isKnown).count();
            int sessionTotal = sessionResults.size();
            int sessionIncorrect = sessionTotal - sessionCorrect;
            int sessionSuccess = session.getScore() != null
                    ? session.getScore()
                    : StatisticsTrackingService.calculateSessionScore(sessionCorrect, sessionTotal);

            Instant studiedAt = session.getCompletedAt() != null
                    ? session.getCompletedAt()
                    : session.getStartedAt();

            progressHistory.add(new UserStatisticsResponse.ProgressHistoryEntry(
                    session.getId(),
                    session.getFlashcardSet().getId(),
                    session.getFlashcardSet().getTitle(),
                    studiedAt,
                    sessionTotal,
                    sessionCorrect,
                    sessionIncorrect,
                    sessionSuccess
            ));
        }

        return new UserStatisticsResponse(
                userId,
                totalCardsStudied,
                correctAnswers,
                incorrectAnswers,
                successPercentage,
                progressHistory
        );
    }
}
