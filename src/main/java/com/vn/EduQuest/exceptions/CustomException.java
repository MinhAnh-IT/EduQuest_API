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

    // Constructor with errorCode and message
    public CustomException(StatusCode errorCode, Object... args) {
        super(errorCode.getMessage(args));
        this.errorCode = errorCode;
    }

    // Constructor với errorCode và cause
    public CustomException(StatusCode errorCode, Throwable cause, Object... args) {
        super(errorCode.getMessage(args), cause);  //message và cause
        this.errorCode = errorCode;
    }
}
