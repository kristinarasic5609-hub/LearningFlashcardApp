package com.flashlearn.app.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "flashcards")
public class Flashcard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;

    @Column(nullable = false)
    private String flashcardSetId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flashcardSetId", insertable = false, updatable = false)
    private FlashcardSet flashcardSet;

    @JsonIgnore
    @OneToMany(mappedBy = "flashcard", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LearningResult> results = new LinkedHashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getFlashcardSetId() {
        return flashcardSetId;
    }

    public void setFlashcardSetId(String flashcardSetId) {
        this.flashcardSetId = flashcardSetId;
    }

    public FlashcardSet getFlashcardSet() {
        return flashcardSet;
    }

    public void setFlashcardSet(FlashcardSet flashcardSet) {
        this.flashcardSet = flashcardSet;
    }
}
