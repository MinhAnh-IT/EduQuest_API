package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Question;
import com.vn.EduQuest.exceptions.CustomException;

public interface QuestionService {
    long getCorrectAnswerId(long questionId) throws CustomException;
    Question getQuestionById(long questionId) throws CustomException;
}
