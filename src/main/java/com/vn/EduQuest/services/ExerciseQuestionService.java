package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.ExerciseQuestion;
import com.vn.EduQuest.exceptions.CustomException;

public interface ExerciseQuestionService {
    ExerciseQuestion getExerciseQuestionById(long exerciseId) throws CustomException;
    void saveExerciseQuestion(ExerciseQuestion exerciseQuestion) throws CustomException;

}
