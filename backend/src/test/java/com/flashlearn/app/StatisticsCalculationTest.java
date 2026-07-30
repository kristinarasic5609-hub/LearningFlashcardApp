package com.flashlearn.app;

import com.flashlearn.app.service.StatisticsTrackingService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsCalculationTest {

    @Test
    void calculatesFullSuccessRate() {
        assertEquals(100, StatisticsTrackingService.calculateSessionScore(5, 5));
    }

    @Test
    void calculatesHalfSuccessRate() {
        assertEquals(50, StatisticsTrackingService.calculateSessionScore(1, 2));
    }

    @Test
    void returnsZeroForEmptySession() {
        assertEquals(0, StatisticsTrackingService.calculateSessionScore(0, 0));
    }

    @Test
    void roundsSuccessRate() {
        assertEquals(67, StatisticsTrackingService.calculateSessionScore(2, 3));
    }
}
