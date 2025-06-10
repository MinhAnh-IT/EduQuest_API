package com.vn.EduQuest.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum StatusCode {
    OK(200, "success"),
    CREATED(201, "Created"),
    NOT_FOUND(404, "Not found %s with parameter %s"),
    LOGIN_FAILED(401, "Login failed"),
    INVALID_TOKEN(402, "Invalid or expired refresh token");
    int code;
    String message;

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
