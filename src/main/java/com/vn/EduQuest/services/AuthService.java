package com.vn.EduQuest.services;

import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.LoginRequest;
import com.vn.EduQuest.payload.request.RefreshTokenRequest;
import com.vn.EduQuest.payload.response.TokenResponse;

public interface AuthService {
    TokenResponse login(LoginRequest request) throws CustomException;
    TokenResponse refreshToken(RefreshTokenRequest refreshToken) throws CustomException;
}
