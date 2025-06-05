package com.vn.EduQuest.services;

import com.vn.EduQuest.payload.response.LoginResponse;

public interface AuthService {
    LoginResponse login(String username, String password);
}
