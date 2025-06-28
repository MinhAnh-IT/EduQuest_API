package com.vn.EduQuest.payload.response.question;

import com.vn.EduQuest.enums.Difficulty;
import com.vn.EduQuest.payload.response.answer.AnswerDetailResponse;
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
public class QuestionDetailResponse {
    long id;
    String content;
    Difficulty difficulty;
    List<AnswerDetailResponse> answers;
}