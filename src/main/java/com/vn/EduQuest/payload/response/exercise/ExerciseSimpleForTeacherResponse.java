package com.vn.EduQuest.payload.response.exercise;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseSimpleForTeacherResponse {
    long id;
    String className;
    String name;
    int durationMinutes;
    int questionCount;
    String status;
    String createdAt;
}
