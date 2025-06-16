package com.vn.EduQuest.payload.request.StudentRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class VerifyOtpRequest {
 
     @NotBlank(message = "Username không được để trống")
    private String username;  // Thay đổi từ email sang username
    
    @NotBlank(message = "Mã OTP không được để trống")
    private String otp;
} 
