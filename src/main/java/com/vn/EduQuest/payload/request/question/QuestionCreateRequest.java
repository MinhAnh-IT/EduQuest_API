package com.vn.EduQuest.payload.request.question;

import com.vn.EduQuest.payload.request.answer.AnswerCreateRequest;
import lombok.Data;
import java.util.List;

@Data
public class QuestionCreateRequest {
    String content;
    String difficulty;
    List<AnswerCreateRequest> answers;
}