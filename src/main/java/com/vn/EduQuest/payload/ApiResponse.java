package com.vn.EduQuest.payload;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.Getter;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ApiResponse<T> {
    int code;
    String message;
    T data;
}