package com.vn.EduQuest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.entities.Participation;
import com.vn.EduQuest.payload.response.QuestionResultDTO;
import com.vn.EduQuest.payload.response.ResultDTO;

@Mapper(componentModel = "spring")
public interface ResultMapper {

    @Mapping(target = "participationId", source = "participation.id")
    @Mapping(target = "exerciseId", source = "exercise.id")
    @Mapping(target = "exerciseName", source = "exercise.name")
    @Mapping(target = "score", source = "participation.score")
    @Mapping(target = "status", source = "participation.status")
    @Mapping(target = "submittedAt", source = "participation.submittedAt")
    @Mapping(target = "questions", source = "questionResultDTOS")
    ResultDTO toResultDTO(Participation participation, Exercise exercise, List<QuestionResultDTO> questionResultDTOS);
}
