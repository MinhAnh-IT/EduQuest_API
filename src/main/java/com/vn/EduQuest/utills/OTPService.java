package com.vn.EduQuest.utills;

public interface OTPService {
    String generateOTP(String username);
    boolean validateOtp(String providedOtp, String storedOtp);
} 