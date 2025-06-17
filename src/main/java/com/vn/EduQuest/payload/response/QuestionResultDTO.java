package com.vn.EduQuest.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class QuestionResultDTO {
    Long questionId;
    String content;
    String difficulty;
    AnswerDTO selectedAnswer;
    AnswerDTO correctAnswer;
}