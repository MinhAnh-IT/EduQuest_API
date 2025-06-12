package com.vn.EduQuest.services;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.UserMapper;
import com.vn.EduQuest.payload.request.ForgotPasswordRequest;
import com.vn.EduQuest.payload.request.LoginRequest;
import com.vn.EduQuest.payload.request.RefreshTokenRequest;
import com.vn.EduQuest.payload.request.ResetPasswordRequest;
import com.vn.EduQuest.payload.request.VerifyOtpRequest;
import com.vn.EduQuest.payload.response.TokenResponse;
import com.vn.EduQuest.repositories.UserRepository;
import com.vn.EduQuest.utills.Bcrypt;
import com.vn.EduQuest.utills.EmailService;
import com.vn.EduQuest.utills.JwtService;
import com.vn.EduQuest.utills.OTPService;
import com.vn.EduQuest.utills.RedisService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    final UserRepository userRepository;
    final OTPService otpService;
    final EmailService emailService;
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
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> {
                return new CustomException(StatusCode.USER_NOT_FOUND,
                    "No account found with this username");
            });        try {
            String otp = otpService.generateOtp(request.getUsername());

            String otpRedisKey = otpCachePrefix + request.getUsername();
            redisService.set(otpRedisKey, otp, otpCacheExpiryMinutes, TimeUnit.MINUTES);
            emailService.sendOtpEmail(user.getEmail(), request.getUsername(), otp);

            return true; // Return true on success
        } catch (Exception e) {
            if (e instanceof CustomException customException) {
                throw customException;
            }
            throw new CustomException(StatusCode.EMAIL_SEND_ERROR,
                "Failed to send OTP: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean verifyOtpForgotPassword(VerifyOtpRequest request) throws CustomException {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> {
                return new CustomException(StatusCode.USER_NOT_FOUND);
            });

        String otpRedisKey = otpCachePrefix + request.getUsername();
        String storedOtp = (String) redisService.get(otpRedisKey);

        if (!otpService.validateOtp(request.getOtp(), storedOtp)) {
            throw new CustomException(StatusCode.INVALID_OTP);
        }

        redisService.delete(otpRedisKey);
        String otpVerifiedKey = otpVerifiedPrefix + user.getUsername(); 
        redisService.set(otpVerifiedKey, "true", otpVerifiedExpiryMinutes, TimeUnit.MINUTES);
        return true; // Return true on success
    }

    @Override
    @Transactional
    public boolean resetPassword(ResetPasswordRequest request) throws CustomException {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> {
                return new CustomException(StatusCode.USER_NOT_FOUND);
            });        String otpVerifiedKey = otpVerifiedPrefix + user.getUsername();
        if (!Boolean.TRUE.equals(redisService.hasKey(otpVerifiedKey))) {
            throw new CustomException(StatusCode.OTP_VERIFICATION_NEEDED);
        }

        try {
            user.setPassword(bcrypt.hashPassword(request.getNewPassword()));
            userRepository.save(user);
            redisService.delete(otpVerifiedKey);
            return true; // Return true on success
        } catch (Exception e) {
            redisService.delete(otpVerifiedKey);
            if (e instanceof CustomException customException) {
                throw customException;
            }
            throw new CustomException(StatusCode.BAD_REQUEST, "Failed to reset password: " + e.getMessage());
        }    }

    @Override
    public boolean logout(String token) throws CustomException {
        try {
            jwtService.validateToken(token);
            String redisKey = blacklistedTokenPrefix + token;
            redisService.set(redisKey, "true", tokenExpiryMinutes, TimeUnit.MINUTES);
            return true; // Return true on success
        } catch (Exception e) {
            log.error("Error during logout, token validation might have failed or token already invalid: {}", e.getMessage());
             if (e instanceof CustomException customException) {
                throw customException;
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