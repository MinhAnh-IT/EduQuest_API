package com.vn.EduQuest.payload.request;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentDetailRequest {
    @NotBlank(message = "Mã số sinh viên không được để trống")
    @Size(min = 8, max = 20, message = "Mã số sinh viên phải từ 8 đến 20 ký tự")
    String studentCode;
} 