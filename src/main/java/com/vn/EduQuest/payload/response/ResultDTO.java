package com.vn.EduQuest.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder

public class ResultDTO {
    private Long participationId;
    private Long exerciseId;
    private String exerciseName;
    private BigDecimal score;
    private String status;
    private LocalDateTime submittedAt;
    private List<QuestionResultDTO> questions;
}