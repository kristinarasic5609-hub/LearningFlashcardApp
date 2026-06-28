package com.flashlearn.app.model.dto;

import jakarta.validation.constraints.Size;

public class UpdateFlashcardRequest {

    @Size(min = 1, max = 500, message = "Question is required")
    private String question;

    @Size(min = 1, max = 500, message = "Answer is required")
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
