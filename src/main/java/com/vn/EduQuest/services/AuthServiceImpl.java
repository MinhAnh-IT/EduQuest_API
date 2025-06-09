package com.vn.EduQuest.services;

import java.util.concurrent.TimeUnit;

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
    private final OTPService otpService;
    private final EmailService emailService;
    private final Jwt jwtUtil;
    private final RedisService redisService;
    private final Bcrypt bcrypt;

    private static final String BLACKLISTED_TOKEN_PREFIX = "blacklisted_token:";
    private static final String OTP_VERIFIED_PREFIX = "otp_verified:"; // Prefix for OTP verified state
    private static final long TOKEN_EXPIRY = 5; // 5 minutes (used for blacklisted logout tokens)
    private static final long OTP_VERIFIED_EXPIRY = 5; // 5 minutes for reset password after OTP verification

    @Override
    @Transactional
    public void initiatePasswordReset(ForgotPasswordRequest request) throws CustomException {
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
            
            log.debug("Sending OTP email to: {}", user.getEmail());
            emailService.sendOtpEmail(user.getEmail(), request.getUsername(), otp);
            
            log.info("Password reset OTP sent successfully for user: {}", request.getUsername());
        } catch (Exception e) {
            log.error("Failed to process password reset OTP request for user: {}", request.getUsername(), e);
            throw new CustomException(StatusCode.EMAIL_SEND_ERROR, 
                "Failed to send OTP: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void verifyOtp(VerifyOtpRequest request) throws CustomException {
        log.info("Verifying OTP for username: {}", request.getUsername());
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> {
                log.warn("User not found during OTP verification for username: {}", request.getUsername());
                return new CustomException(StatusCode.USER_NOT_FOUND);
            });

        if (!otpService.validateOtp(request.getUsername(), request.getOtp())) {
            log.warn("Invalid OTP for username: {}", request.getUsername());
            throw new CustomException(StatusCode.INVALID_OTP);
        }

        // If OTP is valid, clear it and set a flag in Redis indicating OTP verification success
        otpService.clearOtp(request.getUsername());
        String otpVerifiedKey = OTP_VERIFIED_PREFIX + user.getUsername(); // Use username for consistency
        redisService.set(otpVerifiedKey, "true", OTP_VERIFIED_EXPIRY, TimeUnit.MINUTES);
        log.info("OTP verified successfully for username: {}. User can now reset password within {} minutes.", request.getUsername(), OTP_VERIFIED_EXPIRY);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) throws CustomException {
        log.info("Attempting to reset password for username: {}", request.getUsername());
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> {
                 log.warn("User not found during password reset for username: {}", request.getUsername());
                return new CustomException(StatusCode.USER_NOT_FOUND);
            });

        // Check if OTP was verified for this user
        String otpVerifiedKey = OTP_VERIFIED_PREFIX + user.getUsername();
        if (!Boolean.TRUE.equals(redisService.hasKey(otpVerifiedKey))) {
            log.warn("OTP not verified or session expired for username: {}. Cannot reset password.", request.getUsername());
            throw new CustomException(StatusCode.OTP_VERIFICATION_NEEDED);
        }

        try {
            // Validate new password format if necessary (e.g., length, complexity)
            // For now, directly setting it.
            user.setPassword(bcrypt.hashPassword(request.getNewPassword()));
            userRepository.save(user);
            log.info("Password reset successfully for username: {}", request.getUsername());
            
            // Clear the OTP verified flag from Redis as it's a one-time use
            redisService.delete(otpVerifiedKey);
            log.debug("Cleared OTP verified flag for username: {}", request.getUsername());

        } catch (Exception e) {
            log.error("Failed to reset password for username: {}", request.getUsername(), e);
            // Ensure the verified flag is cleared even if an error occurs during password save,
            // though it should ideally be cleared only on success or expiry.
            // If save fails, the user might need to re-verify OTP if the flag is cleared prematurely.
            // For now, clearing it to prevent reuse if an unexpected error occurs after verification.
            redisService.delete(otpVerifiedKey); 
            throw new CustomException(StatusCode.BAD_REQUEST, "Failed to reset password: " + e.getMessage());
        }
    }

    @Override
    public void logout(LogoutRequest request) throws CustomException {
        String token = request.getToken();
        
        try {
            // Verify token is valid before blacklisting (e.g. check signature, not expiry for blacklisting)
            // The current jwtUtil.validateToken might throw an exception if expired, adjust if needed for logout.
            // For blacklisting, we primarily care that it's a token that *was* valid.
            jwtUtil.validateToken(token); // This might need adjustment if it throws error on already expired tokens we still want to blacklist.
                                        // A simpler approach for logout might be to just blacklist without full validation if expiry is an issue.
            
            // Add token to blacklist in Redis with its remaining validity or a fixed short period
            // For simplicity, using TOKEN_EXPIRY, but ideally, it should be the token's actual remaining validity.
            String redisKey = BLACKLISTED_TOKEN_PREFIX + token;
            // Consider getting token's expiry to set an accurate blacklist duration.
            // For now, using a fixed duration.
            redisService.set(redisKey, "true", TOKEN_EXPIRY, TimeUnit.MINUTES); 
            
            log.info("User logged out successfully, token blacklisted: {}", token);
        } catch (Exception e) { // Catching generic Exception from validateToken
            log.error("Error during logout, token validation might have failed or token already invalid: {}", e.getMessage());
            // Depending on policy, you might still blacklist an invalid token to prevent any theoretical reuse.
            // Or throw an error if the token is grossly invalid.
            throw new CustomException(StatusCode.INVALID_TOKEN, "Token validation failed or token already invalid during logout: " + e.getMessage());
        }
    }
}