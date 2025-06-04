package com.vn.EduQuest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.payload.ApiResponse;
import com.vn.EduQuest.payload.request.ForgotPasswordRequest;
import com.vn.EduQuest.payload.request.ResetPasswordRequest;
import com.vn.EduQuest.services.UserService;
import com.vn.EduQuest.utills.EmailService;
import com.vn.EduQuest.utills.OTPService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Processing forgot password request for email: {}", request.getEmail());
        User user = userService.findByEmail(request.getEmail());
        String otp = otpService.generateOtp(user.getEmail());
        emailService.sendOtpEmail(user.getEmail(), otp);
        
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .code(200)
                .message("OTP sent to your email successfully")
                .data(null)
                .build());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Processing reset password request for email: {}", request.getEmail());
        User user = userService.findByEmail(request.getEmail());
        
        if (!otpService.validateOtp(request.getEmail(), request.getOtp())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>builder()
                            .code(400)
                            .message("Invalid or expired OTP")
                            .data(null)
                            .build());
        }
        
        userService.updatePassword(user, request.getNewPassword());
        otpService.clearOtp(request.getEmail());
        
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .code(200)
                .message("Password reset successfully")
                .data(null)
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        // Xóa thông tin phiên hiện tại
        SecurityContextHolder.clearContext();
        
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .code(200)
                .message("Logged out successfully")
                .data(null)
                .build());
    }
}