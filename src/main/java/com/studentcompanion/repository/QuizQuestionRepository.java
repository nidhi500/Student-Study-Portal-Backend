package com.studentcompanion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.studentcompanion.model.CareerGoal;
import com.studentcompanion.model.QuizQuestion;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    @Query("SELECT q FROM QuizQuestion q WHERE q.goal = :goal ORDER BY function('RANDOM')")
    List<QuizQuestion> findRandomQuestionsByGoal(@Param("goal") CareerGoal goal);
}
