package com.vn.EduQuest.payload.request.answer;

import lombok.Data;

@Data
public class AnswerCreateRequest {
    String content;
    Boolean isCorrect;
}
