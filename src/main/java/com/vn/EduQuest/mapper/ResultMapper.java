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
    @Mapping(target = "questions", source = "questionResultDTOS")
    ResultDTO toResultDTO(Participation participation, Exercise exercise,List<QuestionResultDTO> questionResultDTOS);
}