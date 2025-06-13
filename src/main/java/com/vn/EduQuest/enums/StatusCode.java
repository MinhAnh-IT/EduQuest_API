package com.vn.EduQuest.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum StatusCode {
    // 200 - Success
    OK(200, "Success"),
    CREATED(201, "Created"),
    PASSWORD_RESET_SUCCESS(202, "Password reset successful"),
    PASSWORD_RESET_INITIATED(203, "If this email is registered, you will receive a password reset OTP"),
    LOGOUT_SUCCESS(204, "Logged out successfully"),
    OTP_VERIFIED_SUCCESS(205, "OTP verified successfully. You can now reset your password."),
    OTP_SENT(206, "OTP has been sent to your email"),

    // 400 - Client Error
    BAD_REQUEST(400, "Bad request"),
    INVALID_OTP(401, "Invalid or expired OTP"),
    OTP_VERIFICATION_NEEDED(402, "OTP must be verified before resetting the password or the session has expired"),

    // 403 - Client Error (Forbidden)
    USER_NOT_A_STUDENT(403, "User is not a student"),

    // 404 - Client Error (Not Found - Class/Enrollment Specific)
    CLASS_NOT_FOUND_BY_CODE(404, "Class not found with the provided code"),
    CLASS_NOT_FOUND_BY_ID(404, "Class not found with the provided ID"),
    STUDENT_NOT_ENROLLED_IN_CLASS(404, "Student is not enrolled in this class"),

    // 409 - Client Error (Conflict - Enrollment Specific)
    STUDENT_ALREADY_ENROLLED_IN_CLASS(409, "Student is already enrolled in this class"),

    // 410 - Authentication / Authorization
    LOGIN_FAILED(410, "Login failed"),
    EXIST_USERNAME(411, "The username '%s' is already taken by another user"),

    // 420 - Token/Code issues
    INVALID_TOKEN(420, "Invalid or expired refresh token"),
    EXIST_STUDENT_CODE(421, "Student code '%s' already exists"),

    // 430 - Not Found
    USER_NOT_FOUND(430, "User not found"),
    NOT_FOUND(431, "Not found %s with parameter %s"),

    // 440 - Conflict
    USER_ALREADY_ACTIVE(440, "User is already activated"),

    // 450 - Validation
    VALIDATION_ERROR(450, "Validation error"),

    // 460 - Role/Permission
    INVALID_ROLE(460, "User does not have the required role"),

    // 470 - Verification Required
    USER_NOT_VERIFIED(470, "User is not yet verified"),

    // 500 - Server Error
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    EMAIL_SEND_ERROR(501, "Failed to send email");

    int code;
    String message;


    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
