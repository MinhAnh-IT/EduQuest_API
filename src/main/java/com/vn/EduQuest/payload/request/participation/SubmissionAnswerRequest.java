package com.vn.EduQuest.payload.request.participation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmissionAnswerRequest {
    long exerciseQuestionId;
    long selectedAnswerId;
}
