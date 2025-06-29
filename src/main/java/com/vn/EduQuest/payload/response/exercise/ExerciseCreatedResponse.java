package com.vn.EduQuest.payload.response.exercise;

import com.vn.EduQuest.payload.response.question.QuestionDetailResponse;
import com.vn.EduQuest.payload.response.question.QuestionResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseCreatedResponse {
    long id;
    String name;
    String className;
    String startAt;
    String endAt;
    int durationMinutes;
    String status;
    List<QuestionDetailResponse> questions;
}
