package com.vn.EduQuest.services;

import java.util.List;

import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.response.exercise.ExerciseResponse;
import com.vn.EduQuest.payload.response.exercise.InstructorExerciseResponse;
import com.vn.EduQuest.payload.request.exercise.ExerciseRequest;
import com.vn.EduQuest.payload.response.exercise.ExerciseCreatedResponse;
import com.vn.EduQuest.payload.response.exercise.ExerciseDetailForTeacher;
import com.vn.EduQuest.payload.response.exercise.ExerciseSimpleForTeacherResponse;
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

    List<InstructorExerciseResponse> getInstructorExercises(Long instructorId) throws CustomException;
    
    List<InstructorExerciseResponse> getInstructorExercisesByClass(Long instructorId, Long classId) throws CustomException;
    List<ExerciseSimpleForTeacherResponse> getAllExercisesForTeacher(Long userId) throws CustomException;
    List<ExerciseSimpleForTeacherResponse> getExercisesByClassIdForTeacher(Long userId,Long classId) throws CustomException;
    ExerciseDetailForTeacher getExerciseDetailForTeacher(long userId, Long exerciseId) throws CustomException;
    ExerciseCreatedResponse createExercise(long userId, ExerciseRequest exerciseRequest) throws CustomException;
}
