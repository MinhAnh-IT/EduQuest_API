package com.vn.EduQuest.payload.request;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.security.PrivateKey;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    @NotBlank(message = "Tên tài khoản không được để trống")
    @Size(min = 3, max = 50, message = "Tên tài khoản phải từ 3 đến 50 ký tự")

    String username;

    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 100, message = "Tên phải từ 2 đến 100 ký tự")
    String name;

    @NotBlank(message = "Email must not be null")
    @Email(message = "Email invalid")
    String email;

    @NotNull(message = "Password must not be null")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least 1 uppercase letter, 1 lowercase letter, 1 digit, and 1 special character and be at least 8 characters long"
    )
    String password;

    boolean isTeacher;
}
