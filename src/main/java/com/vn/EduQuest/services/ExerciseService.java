package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.response.exerciseQuestion.ExerciseQuestionResponse;

import java.util.List;

public interface ExerciseService {
    List<ExerciseQuestionResponse> getQuestionsByExerciseId(long exerciseId) throws CustomException;
    boolean isExerciseNotExist(long exerciseId);
    Exercise getExerciseById(long exerciseId) throws CustomException;
    int getTotalQuestionsByExerciseId(long exerciseId) throws CustomException;
<<<<<<< HEAD
    List<ExerciseResponse> getExercisesForStudent(Long userId ,Long classId )throws CustomException;

=======
>>>>>>> 7e0c73d (impl api for discussion service and)
}
