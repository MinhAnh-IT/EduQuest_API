package com.vn.EduQuest.payload.response.exercise;

import java.time.LocalDateTime;

import com.vn.EduQuest.enums.ParticipationStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResultResponse {
    private Long participationId;
    private String studentName;
    private String studentCode;
    private String studentEmail;
    private Double score;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private ParticipationStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private String duration;
}
