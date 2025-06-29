package com.vn.EduQuest.payload.response.exercise;

import com.vn.EduQuest.payload.response.question.QuestionDetailResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ExerciseDetailForTeacher {
    long id;
    String name;
    String className;
    int durationMinutes;
    LocalDateTime startAt;
    LocalDateTime endAt;
    int submittedStudentCount;
    List<QuestionDetailResponse> questions;
}
