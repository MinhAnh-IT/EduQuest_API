package com.vn.EduQuest.services;

public interface OTPService {
    String generateOTP(String username);
    boolean validateOTP(String username, String otp);
    void clearOTP(String username);
} 