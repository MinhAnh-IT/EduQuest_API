package com.vn.EduQuest.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AnswerDTO {
    Long answerId;
    String content;
    Boolean isCorrect;
}