package com.vn.EduQuest.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum StatusCode {
    OK(200, "Success"),
    CREATED(201, "Created"),
    BAD_REQUEST(400, "Bad Request"),
    USER_NOT_FOUND(404, "User not found"),
    INVALID_OTP(400, "Invalid or expired OTP"),
    EMAIL_SEND_ERROR(500, "Failed to send email"),    
    PASSWORD_RESET_SUCCESS(200, "Password reset successful"),
    PASSWORD_RESET_INITIATED(200, "If this email is registered, you will receive a password reset OTP"),
    LOGOUT_SUCCESS(200, "Logged out successfully"),
    OTP_VERIFIED_SUCCESS(200, "OTP verified successfully. You can now reset your password."),
    OTP_VERIFICATION_NEEDED(400, "OTP must be verified before password can be reset, or the session has expired."),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_FOUND(404, "Not found %s with parameter %s"),
    LOGIN_FAILED(401, "Login failed"),
    INVALID_TOKEN(402, "Invalid or expired refresh token"),
    USER_NOT_A_STUDENT(403, "Only students can perform this action"),
    CLASS_NOT_FOUND_BY_CODE(404, "Class not found with the provided code"),
    STUDENT_ALREADY_ENROLLED_IN_CLASS(409, "Student is already enrolled in this class");
    
    int code;
    String message;

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
