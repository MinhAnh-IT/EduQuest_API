package com.vn.EduQuest.services;

import java.util.List;

import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.response.Exercise.ExerciseResponse;
import com.vn.EduQuest.payload.response.Exercise.InstructorExerciseResponse;
import com.vn.EduQuest.payload.response.exerciseQuestion.ExerciseQuestionResponse;

import jakarta.validation.constraints.NotNull;

public interface ExerciseService {
    List<ExerciseQuestionResponse> getQuestionsByExerciseId(long exerciseId) throws CustomException;
    boolean isExerciseNotExist(long exerciseId);
    Exercise getExerciseById(long exerciseId) throws CustomException;
    int getTotalQuestionsByExerciseId(long exerciseId) throws CustomException;
    List<ExerciseResponse> getExercisesForStudent(Long userId ,Long classId ) throws CustomException;
    boolean isExpired(@NotNull Long exerciseId) throws CustomException;
    boolean isExerciseAvailable(@NotNull Exercise exercise) throws CustomException;
    
    // Lấy danh sách bài tập của instructor
    List<InstructorExerciseResponse> getInstructorExercises(Long instructorId) throws CustomException;
    
    // Lấy danh sách bài tập của instructor theo lớp
    List<InstructorExerciseResponse> getInstructorExercisesByClass(Long instructorId, Long classId) throws CustomException;
}
