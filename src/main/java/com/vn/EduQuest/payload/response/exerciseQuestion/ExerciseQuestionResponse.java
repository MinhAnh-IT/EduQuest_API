package com.vn.EduQuest.payload.response.exerciseQuestion;

import com.vn.EduQuest.payload.response.question.QuestionResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExerciseQuestionResponse {
    long exerciseQuestionId;
    QuestionResponse question;
}
