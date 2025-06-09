package com.vn.EduQuest.services;

import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.ForgotPasswordRequest;
import com.vn.EduQuest.payload.request.LogoutRequest;
import com.vn.EduQuest.payload.request.ResetPasswordRequest;
import com.vn.EduQuest.payload.request.VerifyOtpRequest; // Added import

public interface AuthService {    
    // Forgot password methods
    void initiatePasswordReset(ForgotPasswordRequest request) throws CustomException;
    void verifyOtp(VerifyOtpRequest request) throws CustomException; // Added method
    void resetPassword(ResetPasswordRequest request) throws CustomException;
    
    // Logout method
    void logout(LogoutRequest request) throws CustomException;
}
