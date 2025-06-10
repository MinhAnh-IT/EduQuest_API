package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.ForgotPasswordRequest;
import com.vn.EduQuest.payload.request.LoginRequest;
import com.vn.EduQuest.payload.request.LogoutRequest;
import com.vn.EduQuest.payload.request.RefreshTokenRequest;
import com.vn.EduQuest.payload.request.ResetPasswordRequest;
import com.vn.EduQuest.payload.request.VerifyOtpRequest;
import com.vn.EduQuest.payload.response.TokenResponse;
import com.vn.EduQuest.repositories.UserRepository;
import com.vn.EduQuest.utills.Bcrypt;
import com.vn.EduQuest.utills.EmailService;
import com.vn.EduQuest.utills.Jwt;
import com.vn.EduQuest.utills.JwtService;
import com.vn.EduQuest.utills.OTPService;
import com.vn.EduQuest.utills.RedisService;
import com.vn.EduQuest.mapper.UserMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    final UserRepository userRepository;
    final OTPService otpService;
    final EmailService emailService;
    final Jwt jwtUtil;
    final JwtService jwtService;
    final RedisService redisService;
    final Bcrypt bcrypt;
    final UserMapper userMapper;

    @Value("${app.otp.cache.prefix}")
    String otpCachePrefix;

    @Value("${app.otp.cache.expiry.minutes}")
    long otpCacheExpiryMinutes;

    @Value("${app.token.blacklisted.prefix}")
    String blacklistedTokenPrefix;

    @Value("${app.otp.verified.prefix}")
    String otpVerifiedPrefix;

    @Value("${app.token.expiry.minutes}")
    long tokenExpiryMinutes;

    @Value("${app.otp.verified.expiry.minutes}")
    long otpVerifiedExpiryMinutes;

    @Value("${EduQuest.jwt.key.refresh-token}")
    String keyRefreshToken;

    @Value("${EduQuest.jwt.refresh.expiration}")
    Long jwtRefreshExpiration;
    
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
             if (e instanceof CustomException) {
                throw (CustomException) e;
            }
            throw new CustomException(StatusCode.INVALID_TOKEN, "Token validation failed or token already invalid during logout: " + e.getMessage());
        }
    }

    @Override
    public TokenResponse login(LoginRequest request) throws CustomException {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new CustomException(StatusCode.NOT_FOUND, "User", request.getUsername())
        );

        if(!Bcrypt.checkPassword(request.getPassword(), user.getPassword())){
            throw new CustomException(StatusCode.LOGIN_FAILED);
        }

        String accessToken = jwtService.generateAccessToken(userMapper.toUserForGenerateToken(user));
        String refreshToken = jwtService.generateRefreshToken(userMapper.toUserForGenerateToken(user));
        redisService.set(keyRefreshToken + user.getId(), refreshToken, jwtRefreshExpiration, TimeUnit.MINUTES);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public TokenResponse refreshToken(RefreshTokenRequest refreshToken) throws CustomException {
        long userId = jwtService.getUserIdFromJWT(refreshToken.getRefreshToken());
        String storedRefreshToken = (String) redisService.get(keyRefreshToken + userId);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken.getRefreshToken())) {
            throw new CustomException(StatusCode.INVALID_TOKEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "User", userId));

        String newRefreshToken = jwtService.generateRefreshToken(userMapper.toUserForGenerateToken(user));
        redisService.set(keyRefreshToken + userId, newRefreshToken, jwtRefreshExpiration, TimeUnit.MINUTES);

        String newAccessToken = jwtService.generateAccessToken(userMapper.toUserForGenerateToken(user));

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}