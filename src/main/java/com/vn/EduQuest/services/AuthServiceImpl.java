package com.vn.EduQuest.services;

import java.util.concurrent.TimeUnit;

import com.vn.EduQuest.utills.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.Role;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.UserMapper;
import com.vn.EduQuest.payload.request.ForgotPasswordRequest;
import com.vn.EduQuest.payload.request.LoginRequest;
import com.vn.EduQuest.payload.request.RefreshTokenRequest;
import com.vn.EduQuest.payload.request.RegisterRequest;
import com.vn.EduQuest.payload.request.ResetPasswordRequest;
import com.vn.EduQuest.payload.request.StudentDetailRequest;
import com.vn.EduQuest.payload.request.VerifyOtpRequest;
import com.vn.EduQuest.payload.response.RegisterRespone;
import com.vn.EduQuest.payload.response.StudentDetailResponse;
import com.vn.EduQuest.payload.response.TokenResponse;
import com.vn.EduQuest.repositories.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {
    final UserRepository userRepository;
    final  UserMapper userMapper;
    final OTPService otpService;
    final EmailService emailService;    final JwtService jwtService;
    final RedisService redisService;

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
            String otp = otpService.generateOTP(request.getUsername());

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
        // Truy cập các trường private thông qua phản ánh (reflection)
        String username;
        String providedOtp;
        try {
            java.lang.reflect.Field usernameField = request.getClass().getDeclaredField("username");
            usernameField.setAccessible(true);
            username = (String) usernameField.get(request);
            
            java.lang.reflect.Field otpField = request.getClass().getDeclaredField("otp");
            otpField.setAccessible(true);
            providedOtp = (String) otpField.get(request);
        } catch (Exception e) {
            throw new CustomException(StatusCode.BAD_REQUEST, "Không thể truy cập dữ liệu từ request");
        }
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                return new CustomException(StatusCode.USER_NOT_FOUND);
            });

        String otpRedisKey = otpCachePrefix + username;
        String storedOtp = (String) redisService.get(otpRedisKey);

        if (!otpService.validateOtp(providedOtp, storedOtp)) {
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
            user.setPassword(Bcrypt.hashPassword(request.getNewPassword()));
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

    @Override
    @Transactional
    public RegisterRespone register(RegisterRequest request) throws CustomException {
        // Validate username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(StatusCode.EXIST_USERNAME, request.getUsername());
        }

        // Create new user
        User user = userMapper.toEntity(request);
        user.setIsActive(false); // User is inactive until OTP verification
        user.setPassword(Bcrypt.hashPassword(request.getPassword()));
        
        // Set role based on isTeacher flag
        if (request.isTeacher()) {
            user.setRole(Role.INSTRUCTOR);
            log.info("Setting role to INSTRUCTOR for user: {}", request.getUsername());
        } else {
            user.setRole(Role.STUDENT);
            log.info("Setting role to STUDENT for user: {}", request.getUsername());
        }

        // Save user to database
        user = userRepository.save(user);
        log.info("Saved user with role: {}", user.getRole());
          // Generate and send OTP
        String otp = otpService.generateOTP(request.getUsername());
        emailService.sendOTPEmail(request.getEmail(), otp);
        
        RegisterRespone response = userMapper.toUserDTO(user);
        log.info("Response role: {}", response.getRole());
        
        return response;
    }

    @Override
    public boolean verifyOTP(VerifyOtpRequest request) throws CustomException {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
            
        if (otpService.validateOTP(request.getUsername(), request.getOtp())) {
            user.setIsActive(true);
            userRepository.save(user);
            otpService.clearOTP(request.getUsername());
            return true;
        }
        
        throw new CustomException(StatusCode.INVALID_OTP);
    }

    @Override
    public boolean sendOTP(String username) throws CustomException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
        if (user.getIsActive()) {
            throw new CustomException(StatusCode.USER_ALREADY_ACTIVE);
        }
        try {
            String otp = otpService.generateOTP(username);
            String email = user.getEmail();
            emailService.sendOTPEmail(email, otp);
            log.info("OTP sent to email: {}", email);
            return true;
        } catch (Exception e) {
            log.error("Error sending OTP: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public StudentDetailResponse updateStudentDetails(Long userId, StudentDetailRequest request) throws CustomException {
        return null;
    }
}