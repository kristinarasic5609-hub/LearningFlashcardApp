package com.flashlearn.app.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "learning_sessions")
public class LearningSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String flashcardSetId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant startedAt;

    private Instant completedAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flashcardSetId", insertable = false, updatable = false)
    private FlashcardSet flashcardSet;

    @JsonIgnore
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("answeredAt ASC")
    private Set<LearningResult> results = new LinkedHashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFlashcardSetId() {
        return flashcardSetId;
    }

    public void setFlashcardSetId(String flashcardSetId) {
        this.flashcardSetId = flashcardSetId;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public FlashcardSet getFlashcardSet() {
        return flashcardSet;
    }

    public void setFlashcardSet(FlashcardSet flashcardSet) {
        this.flashcardSet = flashcardSet;
    }

    public Set<LearningResult> getResults() {
        return results;
    }

    public void setResults(Set<LearningResult> results) {
        this.results = results;
    }
}
