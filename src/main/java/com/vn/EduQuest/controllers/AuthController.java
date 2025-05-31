package com.vn.EduQuest.controllers;

import com.vn.EduQuest.payload.ApiResponse;
import com.vn.EduQuest.payload.request.LoginRequest;
import com.vn.EduQuest.payload.response.LoginResponse;
import com.vn.EduQuest.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/auth")
public class AuthController {
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest request) {

        return ResponseEntity.ok(ApiResponse.builder()
                        .code(200)
                        .message("Login successful")
                        .data(LoginResponse.builder()
                                .accessToken("ajsbdhnja")
                                .build())
                .build());
    }
}
