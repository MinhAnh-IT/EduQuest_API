package com.vn.EduQuest.mapper;

import org.mapstruct.Mapper;

import com.vn.EduQuest.entities.Question;
import com.vn.EduQuest.payload.response.question.QuestionResponse;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    QuestionResponse toQuestionResponse(Question question);
}
