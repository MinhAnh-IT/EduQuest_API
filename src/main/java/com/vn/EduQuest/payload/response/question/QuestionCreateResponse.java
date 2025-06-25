package com.vn.EduQuest.payload.response.question;

import com.vn.EduQuest.payload.response.answer.AnswerResponse;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class QuestionCreateResponse {
    String content;
    List<AnswerResponse> answers;
    String difficulty;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
