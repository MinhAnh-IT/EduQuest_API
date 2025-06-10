package com.vn.EduQuest.services;

import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.ForgotPasswordRequest;
import com.vn.EduQuest.payload.request.LogoutRequest;
import com.vn.EduQuest.payload.request.ResetPasswordRequest;
import com.vn.EduQuest.payload.request.VerifyOtpRequest;

public interface AuthService {    
    // Forgot password methods
    boolean initiatePasswordReset(ForgotPasswordRequest request) throws CustomException;
    boolean verifyOtp(VerifyOtpRequest request) throws CustomException; // Added method
    boolean resetPassword(ResetPasswordRequest request) throws CustomException;
    
    // Logout method
    boolean logout(LogoutRequest request) throws CustomException;
}
