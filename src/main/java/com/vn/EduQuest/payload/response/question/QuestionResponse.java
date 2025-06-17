package com.vn.EduQuest.payload.response.question;

import com.vn.EduQuest.payload.response.answer.AnswerResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionResponse {
    int id;
    String content;
    List<AnswerResponse> answers;
    String difficulty;
}
