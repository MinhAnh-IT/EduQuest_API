package com.vn.EduQuest.services;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.ForgotPasswordRequest;
import com.vn.EduQuest.payload.request.LogoutRequest;
import com.vn.EduQuest.payload.request.ResetPasswordRequest;
import com.vn.EduQuest.payload.request.VerifyOtpRequest;
import com.vn.EduQuest.repositories.UserRepository;
import com.vn.EduQuest.utills.Bcrypt;
import com.vn.EduQuest.utills.EmailService;
import com.vn.EduQuest.utills.Jwt;
import com.vn.EduQuest.utills.OTPService;
import com.vn.EduQuest.utills.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final OTPService otpService; // OTPService for generation/validation logic
    private final EmailService emailService;
    private final Jwt jwtUtil; 
    private final RedisService redisService; // For all caching needs (OTP, verified status, token blacklist)
    private final Bcrypt bcrypt;

    @Value("${app.otp.cache.prefix}")
    private String otpCachePrefix;

    @Value("${app.otp.cache.expiry.minutes}")
    private long otpCacheExpiryMinutes;

    @Value("${app.token.blacklisted.prefix}")
    private String blacklistedTokenPrefix;

    @Value("${app.otp.verified.prefix}")
    private String otpVerifiedPrefix;

    @Value("${app.token.expiry.minutes}")
    private long tokenExpiryMinutes;

    @Value("${app.otp.verified.expiry.minutes}")
    private long otpVerifiedExpiryMinutes;

    @Override
    @Transactional
    public boolean initiatePasswordReset(ForgotPasswordRequest request) throws CustomException {
        log.info("Starting password reset process for username: {}", request.getUsername());
        
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> {
                log.error("User not found: {}", request.getUsername());
                return new CustomException(StatusCode.USER_NOT_FOUND, 
                    "No account found with this username");
            });

        try {
            log.debug("Generating OTP for user: {}", request.getUsername());
            String otp = otpService.generateOtp(request.getUsername());
            
            String otpRedisKey = otpCachePrefix + request.getUsername();
            redisService.set(otpRedisKey, otp, otpCacheExpiryMinutes, TimeUnit.MINUTES);
            log.debug("Stored OTP in Redis for key: {}", otpRedisKey);

            log.debug("Sending OTP email to: {}", user.getEmail());
            emailService.sendOtpEmail(user.getEmail(), request.getUsername(), otp);
            
            log.info("Password reset OTP sent successfully for user: {}", request.getUsername());
            return true; // Return true on success
        } catch (Exception e) {
            log.error("Failed to process password reset OTP request for user: {}", request.getUsername(), e);
            // CustomException will be thrown by the GlobalExceptionHandler, 
            // so we re-throw it here or a new one if needed.
            // The controller will handle forming the ApiResponse with error details.
            if (e instanceof CustomException) {
                throw (CustomException) e;
            }
            throw new CustomException(StatusCode.EMAIL_SEND_ERROR, 
                "Failed to send OTP: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean verifyOtp(VerifyOtpRequest request) throws CustomException {
        log.info("Verifying OTP for username: {}", request.getUsername());
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> {
                log.warn("User not found during OTP verification for username: {}", request.getUsername());
                return new CustomException(StatusCode.USER_NOT_FOUND);
            });

        String otpRedisKey = otpCachePrefix + request.getUsername();
        String storedOtp = (String) redisService.get(otpRedisKey);

        if (!otpService.validateOtp(request.getOtp(), storedOtp)) {
            log.warn("Invalid OTP for username: {}. Provided: {}, Stored: {}", request.getUsername(), request.getOtp(), storedOtp);
            throw new CustomException(StatusCode.INVALID_OTP);
        }

        redisService.delete(otpRedisKey);
        log.debug("Cleared OTP from Redis for key: {}", otpRedisKey);

        String otpVerifiedKey = otpVerifiedPrefix + user.getUsername(); 
        redisService.set(otpVerifiedKey, "true", otpVerifiedExpiryMinutes, TimeUnit.MINUTES);
        log.info("OTP verified successfully for username: {}. User can now reset password within {} minutes.", request.getUsername(), otpVerifiedExpiryMinutes);
        return true; // Return true on success
    }

    @Override
    @Transactional
    public boolean resetPassword(ResetPasswordRequest request) throws CustomException {
        log.info("Attempting to reset password for username: {}", request.getUsername());
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> {
                 log.warn("User not found during password reset for username: {}", request.getUsername());
                return new CustomException(StatusCode.USER_NOT_FOUND);
            });

        String otpVerifiedKey = otpVerifiedPrefix + user.getUsername();
        if (!Boolean.TRUE.equals(redisService.hasKey(otpVerifiedKey))) {
            log.warn("OTP not verified or session expired for username: {}. Cannot reset password.", request.getUsername());
            throw new CustomException(StatusCode.OTP_VERIFICATION_NEEDED);
        }

        try {
            user.setPassword(bcrypt.hashPassword(request.getNewPassword()));
            userRepository.save(user);
            log.info("Password reset successfully for username: {}", request.getUsername());
            
            redisService.delete(otpVerifiedKey);
            log.debug("Cleared OTP verified flag for username: {}", request.getUsername());
            return true; // Return true on success
        } catch (Exception e) {
            log.error("Failed to reset password for username: {}", request.getUsername(), e);
            redisService.delete(otpVerifiedKey); 
            // CustomException will be handled by GlobalExceptionHandler
            if (e instanceof CustomException) {
                throw (CustomException) e;
            }
            throw new CustomException(StatusCode.BAD_REQUEST, "Failed to reset password: " + e.getMessage());
        }
    }

    @Override
    public boolean logout(LogoutRequest request) throws CustomException {
        String token = request.getToken();
        
        try {
            jwtUtil.validateToken(token); 
            String redisKey = blacklistedTokenPrefix + token;
            redisService.set(redisKey, "true", tokenExpiryMinutes, TimeUnit.MINUTES); 
            log.info("User logged out successfully, token blacklisted: {}", token);
            return true; // Return true on success
        } catch (Exception e) { 
            log.error("Error during logout, token validation might have failed or token already invalid: {}", e.getMessage());
            // CustomException will be handled by GlobalExceptionHandler
             if (e instanceof CustomException) {
                throw (CustomException) e;
            }
            throw new CustomException(StatusCode.INVALID_TOKEN, "Token validation failed or token already invalid during logout: " + e.getMessage());
        }
    }
}