package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.entities.ExerciseQuestion;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.response.question.QuestionResponse;

import java.util.List;

public interface ExerciseQuestionService {
    ExerciseQuestion getExerciseQuestionById(long exerciseId) throws CustomException;
    void saveExerciseQuestion(ExerciseQuestion exerciseQuestion) throws CustomException;
    void saveAllExerciseQuestions(Exercise exercise, List<Long> questionIds) throws CustomException;
}
