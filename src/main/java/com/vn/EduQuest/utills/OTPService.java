package com.vn.EduQuest.utills;

public interface OTPService {
    String generateOTP(String username);
    boolean validateOTP(String username, String otp);
    void clearOTP(String username);
    boolean validateOtp(String providedOtp, String storedOtp);
} 