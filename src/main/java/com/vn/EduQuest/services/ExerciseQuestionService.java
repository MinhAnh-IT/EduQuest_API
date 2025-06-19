package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.ExerciseQuestion;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.response.question.QuestionResponse;

public interface ExerciseQuestionService {
    ExerciseQuestion getExerciseQuestionById(long exerciseId) throws CustomException;
    void saveExerciseQuestion(ExerciseQuestion exerciseQuestion) throws CustomException;
}
