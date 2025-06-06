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
    EXIST_USERNAME(401,"The USERNAME %s is already taken by another user");
    int code;
    String message;

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
