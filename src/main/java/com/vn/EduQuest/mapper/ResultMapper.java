package com.vn.EduQuest.mapper;

import com.vn.EduQuest.entities.*;
import com.vn.EduQuest.payload.response.AnswerDTO;
import com.vn.EduQuest.payload.response.QuestionResultDTO;
import com.vn.EduQuest.payload.response.ResultDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ResultMapper {

    @Mapping(target = "participationId", source = "participation.id")
    @Mapping(target = "exerciseId", source = "exercise.id")
    @Mapping(target = "exerciseName", source = "exercise.name")
    @Mapping(target = "score", source = "participation.score")
    @Mapping(target = "status", source = "participation.status")
    @Mapping(target = "submittedAt", source = "participation.submittedAt")
    @Mapping(target = "questions", source = "exerciseQuestions", qualifiedByName = "mapExerciseQuestionsToQuestionResultDTOs")
    ResultDTO toResultDTO(Participation participation, Exercise exercise, List<ExerciseQuestion> exerciseQuestions, @Context List<SubmissionAnswer> submissionAnswers);

    @Mapping(target = "questionId", source = "question.id")
    @Mapping(target = "content", source = "question.content")
    @Mapping(target = "difficulty", source = "question.difficulty")
    @Mapping(target = "point", source = "exerciseQuestion.point")
    @Mapping(target = "order", source = "exerciseQuestion.order")
    @Mapping(target = "selectedAnswer", source = "submissionAnswer.answer", qualifiedByName = "mapAnswerToAnswerDTO")
    @Mapping(target = "correctAnswer", source = "question", qualifiedByName = "mapCorrectAnswer")
    QuestionResultDTO toQuestionResultDTO(ExerciseQuestion exerciseQuestion, SubmissionAnswer submissionAnswer, Question question);

    @Named("mapAnswerToAnswerDTO")
    default AnswerDTO mapAnswerToAnswerDTO(Answer answer) {
        if (answer == null) {
            return null;
        }
        return AnswerDTO.builder()
                .answerId(answer.getId())
                .content(answer.getContent())
                .isCorrect(answer.getIsCorrect())
                .build();
    }

    @Named("mapCorrectAnswer")
    default AnswerDTO mapCorrectAnswer(Question question) {
        if (question == null || question.getAnswers() == null || question.getAnswers().isEmpty()) {
            return null;
        }
        return question.getAnswers().stream()
                .filter(Answer::getIsCorrect)
                .findFirst()
                .map(a -> AnswerDTO.builder()
                        .answerId(a.getId())
                        .content(a.getContent())
                        .isCorrect(a.getIsCorrect())
                        .build())
                .orElse(null);
    }

    @Named("mapExerciseQuestionsToQuestionResultDTOs")
    default List<QuestionResultDTO> mapExerciseQuestionsToQuestionResultDTOs(List<ExerciseQuestion> exerciseQuestions, @Context List<SubmissionAnswer> submissionAnswers) {
        if (exerciseQuestions == null || submissionAnswers == null) {
            return List.of();
        }
        //chatgpt gợi ý sử dụng Map để ánh xạ submissionAnswers theo exerciseQuestionId
        Map<Long, SubmissionAnswer> submissionAnswerMap = submissionAnswers.stream()
                .filter(sa -> sa.getExerciseQuestion() != null)
                .collect(Collectors.toMap(
                        sa -> sa.getExerciseQuestion().getId(),
                        Function.identity(),
                        (sa1, sa2) -> sa1
                ));

        return exerciseQuestions.stream()
                .map(eq -> {
                    SubmissionAnswer sa = submissionAnswerMap.get(eq.getId());
                    Question question = eq.getQuestion();
                    return toQuestionResultDTO(eq, sa, question);
                })
                .sorted((q1, q2) -> Integer.compare(
                        Optional.ofNullable(q1.getOrder()).orElse(0),
                        Optional.ofNullable(q2.getOrder()).orElse(0)
                )) // Sắp xếp theo order
                .toList();
    }
}