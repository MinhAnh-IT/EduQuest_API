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

import com.vn.EduQuest.payload.request.RegisterRequest;
import com.vn.EduQuest.payload.request.SendOtpRequest;
import com.vn.EduQuest.payload.request.StudentDetailRequest;
import com.vn.EduQuest.payload.response.RegisterRespone;
import com.vn.EduQuest.payload.response.StudentDetailResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/auth")
public class AuthController {
    AuthService authService;
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) throws CustomException {
        if (authService.initiatePasswordReset(request)) {
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .code(StatusCode.OK.getCode())
                    .message("OTP has been sent to your email")
                    .build());
        }
        // Fallback for unexpected false return without exception (should not happen based on design)
        return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR.getCode())
                .body(ApiResponse.<String>builder()
                        .code(StatusCode.INTERNAL_SERVER_ERROR.getCode())
                        .message("Failed to initiate password reset.")
                        .build());
    }

    // @PostMapping("/verify-otp")
    // public ResponseEntity<ApiResponse<String>> verifyOtp(
    //         @Valid @RequestBody VerifyOtpRequest request) throws CustomException {
    //     if (authService.verifyOtp(request)) {
    //         return ResponseEntity.ok(ApiResponse.<String>builder()
    //                 .code(StatusCode.OTP_VERIFIED_SUCCESS.getCode())
    //                 .message(StatusCode.OTP_VERIFIED_SUCCESS.getMessage())
    //                 .build());
    //     }
    //     // Fallback for unexpected false return without exception
    //     return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR.getCode())
    //             .body(ApiResponse.<String>builder()
    //                     .code(StatusCode.INTERNAL_SERVER_ERROR.getCode())
    //                     .message("Failed to verify OTP.")
    //                     .build());
    // }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) throws CustomException {
        if (authService.resetPassword(request)) {
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .code(StatusCode.PASSWORD_RESET_SUCCESS.getCode())
                    .message(StatusCode.PASSWORD_RESET_SUCCESS.getMessage())
                    .build());
        }
        // Fallback for unexpected false return without exception
        return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR.getCode())
                .body(ApiResponse.<String>builder()
                        .code(StatusCode.INTERNAL_SERVER_ERROR.getCode())
                        .message("Failed to reset password.")
                        .build());
    }    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authHeader) throws CustomException {
        // Extract token from "Bearer " prefix
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        
        if (authService.logout(token)) {
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .code(StatusCode.LOGOUT_SUCCESS.getCode())
                    .message(StatusCode.LOGOUT_SUCCESS.getMessage())
                    .build());
        }
        // Fallback for unexpected false return without exception
        return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR.getCode())
                .body(ApiResponse.<String>builder()
                        .code(StatusCode.INTERNAL_SERVER_ERROR.getCode())
                        .message("Logout failed.")
                        .build());
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
