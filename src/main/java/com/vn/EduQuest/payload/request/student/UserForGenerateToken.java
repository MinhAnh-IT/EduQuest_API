package com.vn.EduQuest.payload.request.student;

import com.vn.EduQuest.enums.Role;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserForGenerateToken {
    Long id;
    String username;
    Role role;
    boolean isActive;
}