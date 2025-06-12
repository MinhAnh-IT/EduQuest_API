package com.vn.EduQuest.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.ApiResponse;
import com.vn.EduQuest.payload.request.ForgotPasswordRequest;
import com.vn.EduQuest.payload.request.LoginRequest;
import com.vn.EduQuest.payload.request.RefreshTokenRequest;
import com.vn.EduQuest.payload.request.ResetPasswordRequest;
import com.vn.EduQuest.payload.request.VerifyOtpRequest;
import com.vn.EduQuest.payload.response.TokenResponse;
import com.vn.EduQuest.services.AuthService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) throws CustomException {
        authService.initiatePasswordReset(request);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(StatusCode.OK.getCode())
                .message("OTP has been sent to your email") // Specific success message
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp-forgot-password")
    public ResponseEntity<ApiResponse<String>> verifyOtpForgotPassword(
            @Valid @RequestBody VerifyOtpRequest request) throws CustomException {
        authService.verifyOtpForgotPassword(request);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(StatusCode.OK.getCode()) // Changed to OK
                .message("OTP verified successfully. You can now reset your password.") // Specific success message
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) throws CustomException {
        authService.resetPassword(request); 
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(StatusCode.OK.getCode()) // Changed to OK
                .message("Password reset successful") // Specific success message
                .build();
        return ResponseEntity.ok(response);
    }    
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authHeader) throws CustomException {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        authService.logout(token);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(StatusCode.OK.getCode()) // Changed to OK
                .message("Logged out successfully") // Specific success message
                .build();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws CustomException {
        var result = authService.login(request);
        ApiResponse<?> response = ApiResponse.<TokenResponse>builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshToken) throws CustomException {
        var result = authService.refreshToken(refreshToken);
        ApiResponse<?> response = ApiResponse.<TokenResponse>builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }
}