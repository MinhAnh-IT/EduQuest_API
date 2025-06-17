package com.vn.EduQuest.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum StatusCode {
    // 200x - Success
    OK(200, "Success"),
    CREATED(201, "Created"),

    // 400x - Client Error
    BAD_REQUEST(4000, "Bad request"),
    VALIDATION_ERROR(4001, "Validation error"),
    NOT_MATCH(4002, "%s does not match with %s"),
    INVALID_OTP(4003, "Invalid or expired OTP"),
    OTP_VERIFICATION_NEEDED(4004, "OTP must be verified before resetting the password or the session has expired"),
    FORBIDDEN(4005, "Forbidden"),
    INVALID_ROLE(4006, "User does not have the required role"),
    USER_NOT_A_STUDENT(4007, "User is not a student"),

    // 404x - Not Found
    CLASS_NOT_FOUND_BY_CODE(4040, "Class not found with the provided code"),
    CLASS_NOT_FOUND_BY_ID(4041, "Class not found with the provided ID"),
    USER_NOT_FOUND(4042, "User not found"),
    STUDENT_NOT_ENROLLED_IN_CLASS(4043, "Student is not enrolled in this class"),
    NOT_FOUND(4044, "Not found %s with parameter %s"),
    PARTICIPATION_NOT_FOUND(4045, "Participation not found with parameter %s"),
    EXERCISE_NOT_FOUND(4046, "Exercise not found with parameter %s"),
    ANSWER_NOT_FOUND(4047, "Answer not found with parameter %s"),
    EXERCISE_QUESTION_NOT_FOUND(4048, "Exercise question not found with parameter %s"),

    // 409x - Conflict
    STUDENT_ALREADY_ENROLLED_IN_CLASS(4090, "Student is already enrolled in this class"),
    USER_ALREADY_ACTIVE(4091, "User is already activated"),
    EXIST_USERNAME(4092, "The username '%s' is already taken by another user"),
    EXIST_STUDENT_CODE(4093, "Student code '%s' already exists"),
    SUBMISSION_ALREADY_EXISTS(4094, "Submission already exists for this participation"),

    // 410x - Authentication/Authorization
    LOGIN_FAILED(4100, "Login failed"),
    INVALID_TOKEN(4101, "Invalid or expired refresh token"),
    USER_NOT_VERIFIED(4102, "User is not yet verified"),

    // 420x - Exam/Quiz Logic
    PARTICIPATION_NOT_IN_PROGRESS(4200, "Participation is not in progress"),
    PARTICIPATION_ALREADY_SUBMITTED(4201, "Participation already submitted"),
    SUBMISSION_TIME_EXPIRED(4202, "Submission time expired"),
    QUESTION_NOT_ANSWER_CORRECT(4203, "No correct answer found for question with ID %s"),

    // 500x - Server Error
    INTERNAL_SERVER_ERROR(5000, "Internal server error"),
    EMAIL_SEND_ERROR(5001, "Failed to send email");
   

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
    PARTICIPATION_NOT_FOUND(404, "Participation not found for student ID: %s and exercise ID: %s"),
    EXERCISE_NOT_FOUND(404, "Exercise not found with ID: %s"),

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
