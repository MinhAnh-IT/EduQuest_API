package com.vn.EduQuest.payload.response;

import com.vn.EduQuest.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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
    
    // Student details (only populated for STUDENT role)
    private String studentCode;
    private String faculty;
    private Integer enrolledYear;
    private LocalDate birthDate;
}
