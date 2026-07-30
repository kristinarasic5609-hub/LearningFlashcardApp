package com.flashlearn.app.service;

import com.flashlearn.app.model.entity.LearningResult;
import com.flashlearn.app.model.entity.Statistics;
import com.flashlearn.app.repository.LearningResultRepository;
import com.flashlearn.app.repository.StatisticsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Tracks and persists aggregated user learning statistics.
 * StatisticsRepository is the sole persistence layer for Statistics entities.
 */
@Service
public class StatisticsTrackingService {

    private final StatisticsRepository statisticsRepository;
    private final LearningResultRepository learningResultRepository;

    public StatisticsTrackingService(
            StatisticsRepository statisticsRepository,
            LearningResultRepository learningResultRepository) {
        this.statisticsRepository = statisticsRepository;
        this.learningResultRepository = learningResultRepository;
    }

    @Transactional
    public Statistics updateUserStatistics(String userId) {
        List<LearningResult> results = learningResultRepository.findAllByUserId(userId);

        int correctAnswers = (int) results.stream().filter(LearningResult::isKnown).count();
        int wrongAnswers = results.size() - correctAnswers;
        int successRate = results.isEmpty()
                ? 0
                : Math.round((correctAnswers * 100f) / results.size());

        Statistics statistics = statisticsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Statistics created = new Statistics();
                    created.setUserId(userId);
                    return created;
                });

        statistics.setCorrectAnswers(correctAnswers);
        statistics.setWrongAnswers(wrongAnswers);
        statistics.setSuccessRate(successRate);

        return statisticsRepository.save(statistics);
    }

    @Transactional(readOnly = true)
    public Statistics getUserStatistics(String userId) {
        return statisticsRepository.findByUserId(userId).orElse(null);
    }

    public static int calculateSessionScore(int correctCount, int totalCount) {
        if (totalCount == 0) {
            return 0;
        }
        return Math.round((correctCount * 100f) / totalCount);
    }
}
