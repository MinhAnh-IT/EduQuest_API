package com.vn.EduQuest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.vn.EduQuest.payload.request.RegisterRequest;
import com.vn.EduQuest.payload.request.ResetPasswordRequest;
import com.vn.EduQuest.payload.request.StudentDetailRequest;
import com.vn.EduQuest.payload.request.StudentRequest.SendOtpRequest;
import com.vn.EduQuest.payload.request.StudentRequest.VerifyOtpRequest;
import com.vn.EduQuest.payload.response.RegisterRespone;
import com.vn.EduQuest.payload.response.StudentDetailResponse;
import com.vn.EduQuest.payload.response.TokenResponse;
import com.vn.EduQuest.services.AuthService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/auth")
public class AuthController {
    AuthService authService;
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<?>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) throws CustomException {
        boolean result = authService.initiatePasswordReset(request);
        ApiResponse<?> response = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message("OTP has been sent to your email") // Specific success message
                .data(result)
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
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) throws CustomException {
        if (authHeader == null || authHeader.isEmpty()) {
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .code(StatusCode.OK.getCode())
                    .message("No active session to logout")
                    .build();
            return ResponseEntity.ok(response);
        }
        
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        authService.logout(token);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(StatusCode.OK.getCode())
                .message("Logged out successfully")
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
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) throws CustomException {
        RegisterRespone response = authService.register(request);
        ApiResponse<?> apiResponse = ApiResponse.<RegisterRespone>builder()
                .code(StatusCode.CREATED.getCode())
                .message(StatusCode.CREATED.getMessage())
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/students/{userId}/details")
    public ResponseEntity<?> updateStudentDetails(
        @PathVariable Long userId,
        @Valid @RequestBody StudentDetailRequest request) throws CustomException {
    StudentDetailResponse response = authService.updateStudentDetails(userId, request);
    ApiResponse<?> apiResponse = ApiResponse.<StudentDetailResponse>builder()
            .code(StatusCode.CREATED.getCode())
            .message(StatusCode.CREATED.getMessage())
            .data(response)
            .build();
    return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
}

@PostMapping("/verify-otp")
public ResponseEntity<?> verifyOTP(@Valid @RequestBody VerifyOtpRequest request) throws CustomException {
    boolean isVerified = authService.verifyOTP(request);
    ApiResponse<?> apiResponse = ApiResponse.<Boolean>builder()
            .code(isVerified ? StatusCode.OK.getCode() : StatusCode.INVALID_OTP.getCode())
            .message(isVerified ? StatusCode.OK.getMessage() : StatusCode.INVALID_OTP.getMessage())
            .data(isVerified)
            .build();
    return ResponseEntity.ok(apiResponse);
}

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOTP(@RequestBody SendOtpRequest request) throws CustomException {
        Boolean isSent = authService.sendOTP(request.getUsername()); // Assuming the service always sends the OTP successfully
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(isSent)
                .build();
        return ResponseEntity.ok(apiResponse);
    }   
}
