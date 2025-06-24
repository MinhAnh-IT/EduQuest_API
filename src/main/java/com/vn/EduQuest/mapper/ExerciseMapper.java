package com.vn.EduQuest.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.entities.ExerciseClass;
import com.vn.EduQuest.payload.response.Exercise.ExerciseResponse;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {
    ExerciseResponse toResponse(Exercise exercise);
     @AfterMapping
    default void setClassId(@MappingTarget ExerciseResponse response, Exercise exercise) {
        if (exercise.getExerciseClasses() != null && !exercise.getExerciseClasses().isEmpty()) {
            // Lấy classId của lớp đầu tiên (nếu 1 bài kiểm tra chỉ thuộc 1 lớp)
            ExerciseClass exerciseClass = exercise.getExerciseClasses().get(0);
            if (exerciseClass.getClazz() != null) {
                response.setClassId(exerciseClass.getClazz().getId());
            }
        }
    }
}