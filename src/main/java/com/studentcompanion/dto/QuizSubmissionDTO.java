package com.studentcompanion.dto;

public class QuizSubmissionDTO {
    private Long questionId;
    private String selectedAnswer;

    public Long getQuestionId() { return questionId; }

    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getSelectedAnswer() { return selectedAnswer; }

    public void setSelectedAnswer(String selectedAnswer) { this.selectedAnswer = selectedAnswer; }
}
