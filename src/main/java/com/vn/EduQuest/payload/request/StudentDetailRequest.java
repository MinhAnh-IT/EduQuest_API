package com.vn.EduQuest.payload.request;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentDetailRequest {
    @NotBlank(message = "Mã số sinh viên không được để trống")
    @Size(min = 8, max = 20, message = "Mã số sinh viên phải từ 8 đến 20 ký tự")
    String studentCode;

    @NotBlank(message = "Khoa không được để trống")
    String faculty;

    @NotNull(message = "Năm nhập học không được để trống")
    @Min(value = 2000, message = "Năm nhập học không hợp lệ")
    @Max(value = 2100, message = "Năm nhập học không hợp lệ")
    Integer enrolledYear;

    @NotNull(message = "Ngày sinh không được để trống")
    LocalDate birthDate;
} 