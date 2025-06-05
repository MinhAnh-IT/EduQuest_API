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
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    USER_NOT_FOUND(404, "User not found"),
    INVALID_OTP(400, "Invalid or expired OTP"),
    EMAIL_SEND_ERROR(500, "Failed to send email"),
    PASSWORD_RESET_SUCCESS(200, "Password reset successful"),
    PASSWORD_RESET_INITIATED(200, "If this email is registered, you will receive a password reset OTP");

    int code;
    String message;
}
