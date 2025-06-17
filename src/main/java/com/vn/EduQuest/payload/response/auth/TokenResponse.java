package com.vn.EduQuest.payload.response.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {
    String accessToken;
    String refreshToken;
}
