package com.flashlearn.app.repository;

import com.flashlearn.app.model.entity.FlashcardSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FlashcardSetRepository extends JpaRepository<FlashcardSet, String> {

    @Query("""
            SELECT DISTINCT s FROM FlashcardSet s
            LEFT JOIN FETCH s.flashcards
            LEFT JOIN FETCH s.owner
            WHERE s.isPublic = true
            ORDER BY s.updatedDate DESC
            """)
    List<FlashcardSet> findPublicWithDetails();

    @Query("""
            SELECT DISTINCT s FROM FlashcardSet s
            LEFT JOIN FETCH s.flashcards
            LEFT JOIN FETCH s.owner
            ORDER BY s.updatedDate DESC
            """)
    List<FlashcardSet> findAllWithDetails();

    @Query("""
            SELECT DISTINCT s FROM FlashcardSet s
            LEFT JOIN FETCH s.flashcards
            WHERE s.ownerId = :ownerId
            ORDER BY s.updatedDate DESC
            """)
    List<FlashcardSet> findByOwnerIdWithFlashcards(@Param("ownerId") String ownerId);

    @Query("""
            SELECT DISTINCT s FROM FlashcardSet s
            LEFT JOIN FETCH s.flashcards
            LEFT JOIN FETCH s.owner
            WHERE s.id = :id
            """)
    Optional<FlashcardSet> findByIdWithDetails(@Param("id") String id);

    @Query("""
            SELECT DISTINCT s FROM FlashcardSet s
            LEFT JOIN FETCH s.flashcards
            LEFT JOIN FETCH s.owner
            WHERE s.isPublic = true
            AND (LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(s.category) LIKE LOWER(CONCAT('%', :query, '%')))
            ORDER BY s.updatedDate DESC
            """)
    List<FlashcardSet> searchPublic(@Param("query") String query);
}
