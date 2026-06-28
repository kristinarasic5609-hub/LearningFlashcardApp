package com.flashlearn.app.repository;

import com.flashlearn.app.model.entity.LearningSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LearningSessionRepository extends JpaRepository<LearningSession, String> {

    @Query("""
            SELECT DISTINCT s FROM LearningSession s
            LEFT JOIN FETCH s.results r
            JOIN FETCH s.flashcardSet fs
            LEFT JOIN FETCH fs.flashcards
            WHERE s.userId = :userId
            ORDER BY s.startedAt DESC
            """)
    List<LearningSession> findByUserIdWithDetails(@Param("userId") String userId);
}