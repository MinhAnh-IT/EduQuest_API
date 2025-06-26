package com.vn.EduQuest.mapper;

import com.vn.EduQuest.entities.Question;
import com.vn.EduQuest.payload.request.question.QuestionCreateRequest;
import com.vn.EduQuest.payload.response.question.QuestionCreateResponse;
import com.vn.EduQuest.payload.response.question.QuestionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "content", source = "content")
    @Mapping(target = "difficulty", source = "difficulty")
    @Mapping(target = "answers", source = "answers")
    Question toEntity(QuestionCreateRequest questionCreateRequest);

    @Mapping(target = "content", source = "content")
    @Mapping(target = "questionId", source = "id")
    @Mapping(target = "difficulty", source = "difficulty")
    @Mapping(target = "answers", source = "answers")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    QuestionCreateResponse toQuestionCreateResponse(Question question);
}
