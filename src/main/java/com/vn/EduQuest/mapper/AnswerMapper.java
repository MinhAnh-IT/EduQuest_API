package com.vn.EduQuest.mapper;

import com.vn.EduQuest.entities.Answer;
import com.vn.EduQuest.payload.response.answer.AnswerInstructorResponse;
import com.vn.EduQuest.payload.response.answer.AnswerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    AnswerResponse toAnswerResponse(Answer answer);
    @Mapping(target = "id", source = "id")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "isCorrect", source = "isCorrect")
    AnswerInstructorResponse toAnswerInstructorResponse(Answer answer);
}
