package com.vn.EduQuest.payload.response;

import com.vn.EduQuest.entities.Answer;
import com.vn.EduQuest.entities.Question;
import com.vn.EduQuest.payload.response.question.QuestionResponse;
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
    Long selectedAnswer;
    Long correctAnswer;
    QuestionResponse question;
}