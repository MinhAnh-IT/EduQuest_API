package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Answer;
import com.vn.EduQuest.entities.Question;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.response.answer.AnswerResponse;
import com.vn.EduQuest.payload.response.question.QuestionResponse;
import com.vn.EduQuest.repositories.AnswerRepository;
import com.vn.EduQuest.repositories.QuestionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionServiceImpl implements QuestionService{
    QuestionRepository questionRepository;
    AnswerRepository answerRepository;

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
    @Override
    public QuestionResponse getQuestionResponseById(long questionId) throws CustomException {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, questionId));

        List<Answer> answers = answerRepository.findByQuestionId(questionId);

        return QuestionResponse.builder()
                .id(Math.toIntExact(question.getId()))
                .content(question.getContent())
                .difficulty(question.getDifficulty().name())
                .answers(AnswerResponse.fromAnswers(answers))
                .build();
    }
}
