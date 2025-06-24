package com.vn.EduQuest.repositories;

import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.entities.ExerciseQuestion;
import com.vn.EduQuest.payload.projection.QuestionWithExerciseQuestionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ExerciseQuestionRepository extends JpaRepository<ExerciseQuestion, Long> {

    @Query("SELECT eq.question as question, eq.id as eqId FROM ExerciseQuestion eq WHERE eq.exercise = :exercise")
    List<QuestionWithExerciseQuestionId> findQuestionsWithIdsByExercise(Exercise exercise);

    int countByExerciseId(long exerciseId);
  
    List<ExerciseQuestion> findByExercise_Id(Long exerciseId);
    // tính số lượng câu hỏi trong bài tập
    // @Query("SELECT COUNT(eq) FROM ExerciseQuestion eq WHERE eq.exercise.id = :exerciseId")
    int countByExercise_Id(Long exerciseId);

}
