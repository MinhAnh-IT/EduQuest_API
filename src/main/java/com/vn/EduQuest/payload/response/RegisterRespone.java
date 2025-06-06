package com.vn.EduQuest.payload.response;

import com.vn.EduQuest.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterRespone {
    private Long id;
    private String username;
    private String name;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Boolean isActive;
    private String avatarUrl;
}
