package com.flashlearn.app.repository;

import com.flashlearn.app.model.entity.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FlashcardRepository extends JpaRepository<Flashcard, String> {

    @Query("""
            SELECT f FROM Flashcard f
            JOIN FETCH f.flashcardSet
            WHERE f.id = :id
            """)
    Optional<Flashcard> findByIdWithSet(@Param("id") String id);

    long countByFlashcardSetId(String flashcardSetId);

    boolean existsByIdAndFlashcardSetId(String id, String flashcardSetId);
}
