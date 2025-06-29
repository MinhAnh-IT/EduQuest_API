package com.vn.EduQuest.mapper;

import org.mapstruct.Mapper;

import com.vn.EduQuest.entities.Class;
import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.payload.response.exercise.ExerciseResponse;
import com.vn.EduQuest.entities.ExerciseQuestion;
import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.payload.request.exercise.ExerciseRequest;
import com.vn.EduQuest.payload.response.exercise.ExerciseCreatedResponse;
import com.vn.EduQuest.payload.response.exercise.ExerciseDetailForTeacher;
import com.vn.EduQuest.payload.response.exercise.ExerciseSimpleForTeacherResponse;
import com.vn.EduQuest.payload.response.question.QuestionDetailResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {QuestionMapper.class})
public interface ExerciseMapper {
    @Mapping(target = "classId", source = "clazz.id")
    ExerciseResponse toResponse(Exercise exercise);
    


    @Mapping(target = "status", expression = "java(getStatus(exercise))")
    @Mapping(target = "questionCount", expression = "java(getQuestionCount(exercise))")
    @Mapping(target = "className", source = "clazz.name")
    ExerciseSimpleForTeacherResponse toSimpleForTeacherResponse(Exercise exercise);

    default Integer getQuestionCount(Exercise exercise) {
        return exercise.getExerciseQuestions() != null ? exercise.getExerciseQuestions().size() : 0;
    }

    default String getStatus(Exercise exercise) {
        if (exercise.getEndAt() != null && exercise.getEndAt().isBefore(java.time.LocalDateTime.now())) {
            return "Ended";
        }
        if (exercise.getStartAt() != null && exercise.getStartAt().isAfter(java.time.LocalDateTime.now())) {
            return "Not started";
        }
        return "Ongoing";
    }

    @Mapping(target = "submittedStudentCount", ignore = true)
    @Mapping(target = "questions", expression = "java(getQuestions(exercise, questionMapper))")
    @Mapping(target = "className", source = "exercise.clazz.name")
    ExerciseDetailForTeacher toDetailResponse(Exercise exercise, @Context QuestionMapper questionMapper);


    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "instructor", source = "instructor")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exerciseQuestions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "clazz", source = "clazz")
    @Mapping(target = "name", source = "exerciseRequest.name")
    Exercise toEntity(ExerciseRequest exerciseRequest, User instructor, Class clazz);

    @Mapping(target = "status", expression = "java(getStatus(exercise))")
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "className", source = "exercise.clazz.name")
    ExerciseCreatedResponse toCreatedResponse(Exercise exercise);

    default List<QuestionDetailResponse> getQuestions(
            Exercise exercise,
            @Context QuestionMapper questionMapper
    ) {
        return exercise.getExerciseQuestions() != null ?
                exercise.getExerciseQuestions().stream()
                        .map(ExerciseQuestion::getQuestion)
                        .map(questionMapper::toQuestionDetailResponse)
                        .toList()
                : List.of();
    }
}