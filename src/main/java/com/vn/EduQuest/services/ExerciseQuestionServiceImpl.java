package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Answer;
import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.entities.ExerciseQuestion;
import com.vn.EduQuest.entities.Question;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.response.answer.AnswerResponse;
import com.vn.EduQuest.payload.response.question.QuestionResponse;
import com.vn.EduQuest.repositories.AnswerRepository;
import com.vn.EduQuest.repositories.ExerciseQuestionRepository;
import com.vn.EduQuest.repositories.QuestionRepository;
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
    QuestionRepository questionRepository;

    @Override
    public ExerciseQuestion getExerciseQuestionById(long exerciseId) throws CustomException {
        return exerciseQuestionRepository.findById(exerciseId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "Exercise", exerciseId));
    }

    @Override
    public void saveExerciseQuestion(ExerciseQuestion exerciseQuestion) throws CustomException {
        exerciseQuestionRepository.save(exerciseQuestion);
    }

    @Override
    public void saveAllExerciseQuestions(Exercise exercise, List<Long> questionIds) throws CustomException {
        for(Long questionId : questionIds) {
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "Question", questionId));
            ExerciseQuestion exerciseQuestion = ExerciseQuestion.builder()
                    .exercise(exercise)
                    .question(question)
                    .build();
            exerciseQuestionRepository.save(exerciseQuestion);
            exercise.getExerciseQuestions().add(exerciseQuestion);
        }
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
