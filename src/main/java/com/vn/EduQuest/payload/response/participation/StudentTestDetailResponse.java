package com.vn.EduQuest.payload.response.participation;

import com.vn.EduQuest.payload.response.QuestionResultDTO;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StudentTestDetailResponse {
    private Long participationId;
    private Long exerciseId;
    private String studentName;
    private String studentCode;
    private String studentEmail;
    private Double score;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private Integer duration; // seconds
    private List<QuestionResultDTO> questions;
}
