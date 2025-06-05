package com.vn.EduQuest.services;

import com.vn.EduQuest.payload.response.LoginResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    @Override
    public LoginResponse login(String username, String password) {

        return null;
    }
}