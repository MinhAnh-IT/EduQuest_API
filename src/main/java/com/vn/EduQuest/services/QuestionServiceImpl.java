package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Question;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.repositories.QuestionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionServiceImpl implements QuestionService{
    QuestionRepository questionRepository;

    @Override
    public long getCorrectAnswerId(long questionId) throws CustomException {
        var question = getQuestionById(questionId);
        for(var answer : question.getAnswers()) {
            if (answer.getIsCorrect()) {
                return answer.getId();
            }
        }
        throw new  CustomException(StatusCode.QUESTION_NOT_ANSWER_CORRECT, questionId);
    }

    @Override
    public Question getQuestionById(long questionId) throws CustomException {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "question", questionId));
    }
}
