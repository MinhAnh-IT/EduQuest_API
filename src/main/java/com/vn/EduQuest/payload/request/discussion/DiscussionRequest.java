package com.vn.EduQuest.payload.request.discussion;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscussionRequest {
    @NotNull(message = "Exercise ID cannot be null")
    Long exerciseId;

    @NotNull(message = "Content cannot be null")
    String content;
}
