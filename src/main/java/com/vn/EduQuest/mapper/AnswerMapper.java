package com.vn.EduQuest.mapper;

import com.vn.EduQuest.entities.Answer;
import com.vn.EduQuest.payload.response.answer.AnswerResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    AnswerResponse toAnswerResponse(Answer answer);
}
