package com.vn.EduQuest.payload.response.discussion;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscussionResponse {
    Long id;
    String content;
    String createdByName;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
