package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.UserMapper;
import com.vn.EduQuest.payload.request.LoginRequest;
import com.vn.EduQuest.payload.request.RefreshTokenRequest;
import com.vn.EduQuest.payload.response.TokenResponse;
import com.vn.EduQuest.repositories.UserRepository;
import com.vn.EduQuest.utills.Bcrypt;
import com.vn.EduQuest.utills.JwtService;
import com.vn.EduQuest.utills.RedisService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {
    final UserRepository userRepository;
    final JwtService jwtService;
    final UserMapper userMapper;
    final RedisService redisService;

    @Value("${EduQuest.jwt.key.refresh-token}")
    String keyRefreshToken;

    @Value("${EduQuest.jwt.refresh.expiration}")
    Long jwtRefreshExpiration;


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