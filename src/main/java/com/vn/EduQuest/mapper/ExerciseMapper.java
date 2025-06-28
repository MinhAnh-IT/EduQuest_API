package com.vn.EduQuest.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.payload.response.Exercise.ExerciseResponse;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {
    ExerciseResponse toResponse(Exercise exercise);
    
    @AfterMapping
    default void setClassId(@MappingTarget ExerciseResponse response, Exercise exercise) {
        if (exercise.getClassId() != null) {
            response.setClassId(exercise.getClassId());
        }
    }
}