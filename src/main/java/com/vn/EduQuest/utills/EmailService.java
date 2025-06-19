package com.vn.EduQuest.utills;

import java.util.concurrent.CompletableFuture;

public interface EmailService {

    void sendOTPEmail(String to, String otp, boolean isResend);

    void sendOtpEmail(String to, String username, String otp);

    // Async methods
    CompletableFuture<Void> sendOtpEmailAsync(String to, String username, String otp);
}
