package com.vn.EduQuest.mapper;

import org.mapstruct.Mapper;

import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.payload.response.Exercise.ExerciseResponse;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {
    ExerciseResponse toResponse(Exercise exercise);
}