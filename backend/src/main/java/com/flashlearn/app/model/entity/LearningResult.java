package com.flashlearn.app.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "learning_results",
        uniqueConstraints = @UniqueConstraint(columnNames = {"sessionId", "flashcardId"})
)
public class LearningResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String sessionId;

    @Column(nullable = false)
    private String flashcardId;

    @Column(nullable = false)
    private boolean known;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant answeredAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessionId", insertable = false, updatable = false)
    private LearningSession session;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flashcardId", insertable = false, updatable = false)
    private Flashcard flashcard;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public boolean isKnown() {
        return known;
    }

    public void setKnown(boolean known) {
        this.known = known;
    }

    public Instant getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(Instant answeredAt) {
        this.answeredAt = answeredAt;
    }
}
