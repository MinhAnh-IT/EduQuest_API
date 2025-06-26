package com.vn.EduQuest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vn.EduQuest.entities.Answer;
import com.vn.EduQuest.entities.ExerciseQuestion;
import com.vn.EduQuest.entities.Participation;
import com.vn.EduQuest.entities.SubmissionAnswer;

@Mapper(componentModel = "spring")
public interface SubmissionAnswerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    SubmissionAnswer toEntity(ExerciseQuestion exerciseQuestion, Answer answer, Participation participation);
}
