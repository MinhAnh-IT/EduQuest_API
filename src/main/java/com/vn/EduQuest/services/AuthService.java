package com.vn.EduQuest.services;

import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.RegisterRequest;
import com.vn.EduQuest.payload.response.LoginResponse;
import com.vn.EduQuest.payload.response.RegisterRespone;

public interface AuthService {
    LoginResponse login(String username, String password);
    RegisterRespone register(RegisterRequest request) throws CustomException;
}
