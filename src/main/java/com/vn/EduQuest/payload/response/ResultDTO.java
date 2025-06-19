package com.vn.EduQuest.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ResultDTO {
    Long participationId;
    Long exerciseId;
    String exerciseName;
    BigDecimal score;
    String status;
    LocalDateTime submittedAt;
    List<QuestionResultDTO> questions;
}