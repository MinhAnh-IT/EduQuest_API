package com.vn.EduQuest.repositories;

import com.vn.EduQuest.entities.ExerciseQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;

public interface ExerciseQuestionRepository extends JpaRepository<ExerciseQuestion, Long> {
    List<ExerciseQuestion> findByExercise_Id(Long exerciseId);
}
