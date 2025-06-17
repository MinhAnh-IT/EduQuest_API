package com.vn.EduQuest.payload.response.participation;

import com.vn.EduQuest.enums.ParticipationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SubmissionAnswerResponse {
    long participationId;
    ParticipationStatus status;
    LocalDateTime submittedAt;
}
