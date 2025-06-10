package com.vn.EduQuest.exceptions;

import com.vn.EduQuest.enums.StatusCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomException extends Exception {
    StatusCode errorCode;

    public CustomException(StatusCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(StatusCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public CustomException(StatusCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public CustomException(StatusCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}