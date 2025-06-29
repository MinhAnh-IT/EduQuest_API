package com.vn.EduQuest.payload.request.exercise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseRequest {
    @NotBlank(message = "Name cannot be blank")
    String name;
    @NotBlank(message = "Description cannot be blank")
    long classId;
    @NotBlank(message = "Description cannot be blank")
    int durationMinutes;
    @NotBlank(message = "Start time cannot be blank")
    String startAt;
    @NotBlank(message = "End time cannot be blank")
    String endAt;
    @NotNull(message = "Question IDs cannot be null")
    List<Long> questionIds;
}
