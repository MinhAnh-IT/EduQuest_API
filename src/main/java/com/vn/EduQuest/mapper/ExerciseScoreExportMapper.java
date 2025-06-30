package com.vn.EduQuest.mapper;

import com.vn.EduQuest.entities.Participation;
import com.vn.EduQuest.payload.response.exercise.ExerciseScoreExport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ExerciseScoreExportMapper {
    @Mappings({
        @Mapping(target = "studentCode", source = "student.studentCode"),
        @Mapping(target = "name", source = "student.user.name"),
        @Mapping(target = "score", expression = "java(java.math.BigDecimal.valueOf(participation.getScore()))")
        // Removed any mapping for "className" as it does not exist in ExerciseScoreExport
    })
    ExerciseScoreExport toDto(Participation participation);
}