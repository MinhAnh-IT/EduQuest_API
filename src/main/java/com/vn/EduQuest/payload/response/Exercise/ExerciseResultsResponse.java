package com.vn.EduQuest.payload.response.Exercise;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseResultsResponse {
    private Long exerciseId;
    private String exerciseName;
    private Integer totalQuestions;
    private Integer totalParticipants;
    private List<StudentResultResponse> studentResults;
}
