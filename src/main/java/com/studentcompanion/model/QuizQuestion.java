package com.studentcompanion.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal")
    private CareerGoal goal;

    private String question;

    @ElementCollection
    private List<String> options;

    private String correctAnswer;

    // Constructors
    public QuizQuestion(String question, List<String> options, String correctAnswer, CareerGoal goal) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.goal = goal;
    }

    public QuizQuestion() {}

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CareerGoal getGoal() {   // ✅ fixed getter
        return goal;
    }

    public void setGoal(CareerGoal goal) {  // ✅ fixed setter
        this.goal = goal;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}

