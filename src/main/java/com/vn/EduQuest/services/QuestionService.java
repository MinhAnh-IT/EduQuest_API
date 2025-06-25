package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Question;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.response.question.QuestionCreateResponse;
import com.vn.EduQuest.payload.response.question.QuestionResponse;

import java.util.List;

public interface QuestionService {
    long getCorrectAnswerId(long questionId) throws CustomException;
    Question getQuestionById(long questionId) throws CustomException;
    QuestionResponse getQuestionResponseById(long questionId) throws CustomException;
    QuestionCreateResponse createQuestion(Question question, Long instructorId) throws CustomException;
    QuestionCreateResponse updateQuestion(Long QuestionID,Question question, Long instructorId) throws CustomException;
    List<QuestionCreateResponse> getQuestionsCreatedByInstructor(Long instructorId) throws CustomException;
}
