package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Answer;
import com.vn.EduQuest.entities.ExerciseQuestion;
import com.vn.EduQuest.entities.Question;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.response.answer.AnswerResponse;
import com.vn.EduQuest.payload.response.question.QuestionResponse;
import com.vn.EduQuest.repositories.AnswerRepository;
import com.vn.EduQuest.repositories.ExerciseQuestionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseQuestionServiceImpl implements ExerciseQuestionService{
    ExerciseQuestionRepository exerciseQuestionRepository;
    AnswerRepository answerRepository;

    @Override
    public ExerciseQuestion getExerciseQuestionById(long exerciseId) throws CustomException {
        return exerciseQuestionRepository.findById(exerciseId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "Exercise", exerciseId));
    }

    @Override
    public void saveExerciseQuestion(ExerciseQuestion exerciseQuestion) throws CustomException {
        exerciseQuestionRepository.save(exerciseQuestion);
    }
    public QuestionResponse getQuestionResponseByExerciseQuestion(long exerciseQuestionId) throws CustomException {
        ExerciseQuestion exerciseQuestion = exerciseQuestionRepository.findById(exerciseQuestionId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "Exercise Question", exerciseQuestionId));

        Question question = exerciseQuestion.getQuestion();
        List<Answer> answers = answerRepository.findByQuestionId(question.getId());

        return QuestionResponse.builder()
                .id(Math.toIntExact(question.getId()))
                .content(question.getContent())
                .difficulty(question.getDifficulty().name())
                .answers(AnswerResponse.fromAnswers(answers))
                .build();
    }
}
