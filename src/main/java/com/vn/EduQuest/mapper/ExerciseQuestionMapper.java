package com.vn.EduQuest.mapper;

import com.vn.EduQuest.entities.Question;
import com.vn.EduQuest.payload.response.exerciseQuestion.ExerciseQuestionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExerciseQuestionMapper {


    ExerciseQuestionResponse toExerciseQuestionResponse(Question question, Long exerciseQuestionId);
}
