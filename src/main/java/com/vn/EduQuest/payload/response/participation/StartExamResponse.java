package com.vn.EduQuest.payload.response.participation;

import com.vn.EduQuest.entities.Student;
import com.vn.EduQuest.enums.ParticipationStatus;
import com.vn.EduQuest.payload.response.exerciseQuestion.ExerciseQuestionResponse;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class StartExamResponse {
    Long participationId;
    Long exerciseId;
    Long studentId;
    LocalDateTime startAt;
    Integer durationMinutes;
    @Enumerated(EnumType.STRING)
    ParticipationStatus status;
    List<ExerciseQuestionResponse> questions;
}
