package com.flashlearn.app.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateFlashcardSetRequest {

    @NotBlank(message = "Title is required (max 100 characters)")
    @Size(max = 100, message = "Title is required (max 100 characters)")
    private String title;

    @Size(max = 500, message = "Description max 500 characters")
    private String description = "";

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category is required")
    private String category;

    private Boolean isPublic = true;

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

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
}
