package com.studentcompanion.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studentcompanion.dto.QuizSubmissionDTO;
import com.studentcompanion.model.CareerGoal;
import com.studentcompanion.model.QuizQuestion;
import com.studentcompanion.repository.QuizQuestionRepository;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    // ✅ Fetch 20 random questions by goal (case-insensitive and safe)
    @GetMapping("/{goal}")
public ResponseEntity<?> getQuizByGoal(@PathVariable String goal) {
    try {
        CareerGoal enumGoal = CareerGoal.valueOf(goal.toUpperCase());
        List<QuizQuestion> all = quizQuestionRepository.findRandomQuestionsByGoal(enumGoal);

        if (all == null || all.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList()); // ✅ No crash, just empty
        }

        List<QuizQuestion> top20 = all.size() > 20 ? all.subList(0, 20) : all;
        return ResponseEntity.ok(top20);

    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(Collections.singletonMap("error", "Invalid career goal: " + goal));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError()
                .body(Collections.singletonMap("error", "Server error while fetching quiz"));
    }
}

    // ✅ Submit quiz answers and return score
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Integer>> submitQuiz(@RequestBody List<QuizSubmissionDTO> submissions) {
        AtomicInteger correct = new AtomicInteger(0);
        int total = submissions.size();

        submissions.forEach(submission -> {
            quizQuestionRepository.findById(submission.getQuestionId()).ifPresent(question -> {
                if (question.getCorrectAnswer().equalsIgnoreCase(submission.getSelectedAnswer())) {
                    correct.incrementAndGet();
                }
            });
        });

        Map<String, Integer> result = new HashMap<>();
        result.put("correct", correct.get());
        result.put("wrong", total - correct.get());
        return ResponseEntity.ok(result);
    }
}
