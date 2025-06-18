package com.vn.EduQuest.payload.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
@Data
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRequest {
    @NotBlank(message = "Email không được để trống")
    @Email (message = "Email không hợp lệ ")
    String email;
    String avatarUrl ;
}
