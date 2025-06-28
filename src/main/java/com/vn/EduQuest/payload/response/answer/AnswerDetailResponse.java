package com.vn.EduQuest.payload.response.answer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerDetailResponse {
    long id;
    String content;
    Boolean isCorrect;
}
