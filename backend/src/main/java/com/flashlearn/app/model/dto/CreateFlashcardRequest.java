package com.flashlearn.app.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateFlashcardRequest {

    @NotBlank(message = "Question is required")
    @Size(max = 500, message = "Question is required")
    private String question;

    @NotBlank(message = "Answer is required")
    @Size(max = 500, message = "Answer is required")
    private String answer;

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
}
