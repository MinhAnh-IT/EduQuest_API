package com.vn.EduQuest.services;

import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.ForgotPasswordRequest;
import com.vn.EduQuest.payload.request.LoginRequest;
import com.vn.EduQuest.payload.request.LogoutRequest;
import com.vn.EduQuest.payload.request.RefreshTokenRequest;
import com.vn.EduQuest.payload.request.ResetPasswordRequest;
import com.vn.EduQuest.payload.request.VerifyOtpRequest;
import com.vn.EduQuest.payload.response.TokenResponse;

public interface AuthService {    
    // Forgot password methods
    boolean initiatePasswordReset(ForgotPasswordRequest request) throws CustomException;
    boolean verifyOtp(VerifyOtpRequest request) throws CustomException; // Added method
    boolean resetPassword(ResetPasswordRequest request) throws CustomException;
    
    // Logout method
    boolean logout(LogoutRequest request) throws CustomException;
    
    TokenResponse login(LoginRequest request) throws CustomException;
    TokenResponse refreshToken(RefreshTokenRequest refreshToken) throws CustomException;
}
