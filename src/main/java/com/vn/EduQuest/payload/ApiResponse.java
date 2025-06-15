package com.vn.EduQuest.payload;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor
public class ApiResponse<T> {
    int code;
    String message;
    T data;

    // Constructor với tất cả tham số (tùy chọn)
    @Builder
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}