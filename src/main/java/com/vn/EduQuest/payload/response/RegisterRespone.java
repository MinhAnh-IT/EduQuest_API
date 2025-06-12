package com.vn.EduQuest.payload.response;

import com.vn.EduQuest.enums.Role;
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
    private Role role;
    private Boolean isActive;
    private String avatarUrl;
}
