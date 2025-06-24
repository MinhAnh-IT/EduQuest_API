package com.vn.EduQuest.payload.response.Exercise;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class ExerciseResponse {
    private Long id;
    private String name;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer durationMinutes;
    private String status; 
    private Integer questionCount;
    private Long classId; // <-- Thêm dòng này
}
