package com.vn.EduQuest.payload.request.participation;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubmissionExamRequest {
    long participationId;
    List<SubmissionAnswerRequest> selectedAnswers;
}
