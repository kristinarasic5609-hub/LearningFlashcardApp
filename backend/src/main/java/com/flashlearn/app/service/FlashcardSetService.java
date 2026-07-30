package com.flashlearn.app.service;

import com.flashlearn.app.exception.AppException;
import com.flashlearn.app.model.dto.AuthUserDto;
import com.flashlearn.app.model.dto.CreateFlashcardSetRequest;
import com.flashlearn.app.model.dto.UpdateFlashcardSetRequest;
import com.flashlearn.app.model.entity.FlashcardSet;
import com.flashlearn.app.repository.FlashcardSetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FlashcardSetService {

    private final FlashcardSetRepository flashcardSetRepository;

    public FlashcardSetService(FlashcardSetRepository flashcardSetRepository) {
        this.flashcardSetRepository = flashcardSetRepository;
    }

    @Transactional(readOnly = true)
    public List<FlashcardSet> listPublic(String search) {
        if (search != null && !search.trim().isEmpty()) {
            return flashcardSetRepository.searchPublic(search.trim());
        }
        return flashcardSetRepository.findPublicWithDetails();
    }

    @Transactional(readOnly = true)
    public List<FlashcardSet> listByOwner(String ownerId) {
        return flashcardSetRepository.findByOwnerIdWithFlashcards(ownerId);
    }

    @Transactional(readOnly = true)
    public FlashcardSet getById(String id, AuthUserDto user) {
        FlashcardSet set = flashcardSetRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new AppException(404, "Flashcard set not found"));

        if (!set.isPublic() && (user == null || !set.getOwnerId().equals(user.id()))) {
            throw new AppException(403, "Access denied");
        }

        return set;
    }

    @Transactional
    public FlashcardSet create(String ownerId, CreateFlashcardSetRequest request) {
        FlashcardSet set = new FlashcardSet();
        set.setTitle(request.getTitle());
        set.setDescription(request.getDescription() != null ? request.getDescription() : "");
        set.setCategory(request.getCategory());
        set.setPublic(request.getIsPublic() != null ? request.getIsPublic() : true);
        set.setOwnerId(ownerId);
        set = flashcardSetRepository.save(set);
        return flashcardSetRepository.findByIdWithDetails(set.getId()).orElse(set);
    }

    @Transactional
    public FlashcardSet update(String id, AuthUserDto user, UpdateFlashcardSetRequest request) {
        FlashcardSet set = getOwnedSet(id, user.id());

        if (request.getTitle() != null) {
            set.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            set.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            set.setCategory(request.getCategory());
        }
        if (request.getIsPublic() != null) {
            set.setPublic(request.getIsPublic());
        }

        flashcardSetRepository.save(set);
        return flashcardSetRepository.findByIdWithDetails(id).orElse(set);
    }

    @Transactional
    public void delete(String id, AuthUserDto user) {
        FlashcardSet set = getOwnedSet(id, user.id());
        flashcardSetRepository.delete(set);
    }

    private FlashcardSet getOwnedSet(String id, String ownerId) {
        FlashcardSet set = flashcardSetRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new AppException(404, "Flashcard set not found"));

        if (!set.getOwnerId().equals(ownerId)) {
            throw new AppException(403, "You can only modify your own flashcard sets");
        }

        return set;
    }
}
