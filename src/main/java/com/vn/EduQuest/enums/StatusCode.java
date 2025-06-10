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
    EXIST_USERNAME(401,"The USERNAME %s is already taken by another user"),
    EXIST_STUDENT_CODE(402, "Mã số sinh viên %s đã tồn tại"),
    INVALID_OTP(403, "Mã OTP không hợp lệ hoặc đã hết hạn"),
    OTP_SENT(200, "Mã OTP đã được gửi đến email của bạn"),
    USER_NOT_FOUND(404, "Không tìm thấy người dùng"),
    USER_ALREADY_ACTIVE(405, "Người dùng đã được kích hoạt trước đó"),
    VALIDATION_ERROR(406, "Validation error"),
    USER_NOT_VERIFIED(408, "Người dùng chưa được xác minh"),
    INVALID_ROLE(407, "Người dùng không phải là sinh viên");
    

    int code;
    String message;

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
