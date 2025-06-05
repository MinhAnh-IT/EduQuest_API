package com.vn.EduQuest.services;

import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.ForgotPasswordRequest;
import com.vn.EduQuest.payload.request.ResetPasswordRequest;
import com.vn.EduQuest.payload.response.LoginResponse;
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

    private static final String RESET_TOKEN_PREFIX = "reset_token:";
    private static final long TOKEN_EXPIRY = 5; // 5 minutes

    @Override
    public LoginResponse login(String username, String password) throws CustomException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
            
        if (!bcrypt.matches(password, user.getPassword())) {
            throw new CustomException(StatusCode.BAD_REQUEST, "Invalid password");
        }

        String token = jwtUtil.generatePasswordResetToken(user.getId(), user.getEmail());
        return new LoginResponse(token, username, user.getRole().toString());
    }

    @Override
    @Transactional
    public void initiatePasswordReset(ForgotPasswordRequest request) throws CustomException {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND, 
                "No account found with this username"));

        try {
            String resetToken = jwtUtil.generatePasswordResetToken(user.getId(), user.getEmail());
            String redisKey = RESET_TOKEN_PREFIX + user.getId();
            redisService.set(redisKey, resetToken, TOKEN_EXPIRY, TimeUnit.MINUTES);

            String otp = otpService.generateOtp(request.getUsername());
            emailService.sendOtpEmail(user.getEmail(), request.getUsername(), otp);
        } catch (Exception e) {
            log.error("Failed to process password reset request", e);
            throw new CustomException(StatusCode.EMAIL_SEND_ERROR, 
                "Failed to process password reset request");
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) throws CustomException {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));

        if (!otpService.validateOtp(request.getUsername(), request.getOtp())) {
            throw new CustomException(StatusCode.INVALID_OTP);
        }

        String redisKey = RESET_TOKEN_PREFIX + user.getId();
        String storedToken = (String) redisService.get(redisKey);
        
        if (storedToken == null) {
            throw new CustomException(StatusCode.INVALID_OTP, "Password reset session expired");
        }

        try {
            jwtUtil.validateToken(storedToken);
            user.setPassword(bcrypt.hashPassword(request.getNewPassword()));
            userRepository.save(user);
            
            otpService.clearOtp(request.getUsername());
            redisService.delete(redisKey);
        } catch (Exception e) {
            log.error("Failed to reset password", e);
            redisService.delete(redisKey);
            throw new CustomException(StatusCode.BAD_REQUEST, "Failed to reset password");
        }
    }
}