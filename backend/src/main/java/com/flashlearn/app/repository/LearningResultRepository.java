package com.flashlearn.app.repository;

import com.flashlearn.app.model.entity.LearningResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LearningResultRepository extends JpaRepository<LearningResult, String> {

    Optional<LearningResult> findBySessionIdAndFlashcardId(String sessionId, String flashcardId);

    long countBySessionId(String sessionId);

    List<LearningResult> findBySessionId(String sessionId);

    @Query("""
            SELECT r FROM LearningResult r
            JOIN r.session s
            WHERE s.userId = :userId
            ORDER BY r.answeredAt DESC
            """)
    List<LearningResult> findAllByUserId(@Param("userId") String userId);
}
