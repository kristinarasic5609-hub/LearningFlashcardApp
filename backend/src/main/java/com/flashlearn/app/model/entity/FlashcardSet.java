package com.flashlearn.app.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.flashlearn.app.model.dto.OwnerRef;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "flashcard_sets")
public class FlashcardSet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String category;

    @JsonProperty("isPublic")
    @Column(nullable = false)
    private boolean isPublic = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedDate;

    @Column(nullable = false)
    private String ownerId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId", insertable = false, updatable = false)
    private User owner;

    @JsonProperty("owner")
    public OwnerRef getOwnerSummary() {
        if (owner == null) {
            return null;
        }
        return new OwnerRef(ownerId, null);
    }

    @OneToMany(mappedBy = "flashcardSet", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private Set<Flashcard> flashcards = new LinkedHashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "flashcardSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LearningSession> learningSessions = new LinkedHashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @JsonIgnore
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<Flashcard> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(Set<Flashcard> flashcards) {
        this.flashcards = flashcards;
    }
}
