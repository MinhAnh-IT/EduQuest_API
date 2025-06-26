package com.vn.EduQuest.payload.response.Exercise;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorExerciseResponse {
    private Long exerciseId;
    private String exerciseName;
    private String description;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer durationMinutes;
    private String status; // UPCOMING, ACTIVE, EXPIRED
    private LocalDateTime createdAt;
    
    // Statistics
    private Integer totalQuestions;
    private Integer totalParticipants;
    private Integer submittedCount;
    private Integer inProgressCount;
    
    // Class information
    private Long classId;
    private String className;
}
