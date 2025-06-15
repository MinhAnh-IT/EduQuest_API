package com.vn.EduQuest.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequest {
    @NotBlank(message = "Token is required")
    private String token;
}