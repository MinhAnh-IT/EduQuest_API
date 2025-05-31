package com.vn.EduQuest.payload;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse <T>{
    int code;
    String message;
    T data;
}
