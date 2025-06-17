package com.vn.EduQuest.payload.response.student;


import com.vn.EduQuest.enums.Role;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class StudentDetailResponse {
     private Long id;
    private String username;
    private String name;
    private String email;
    private Role role;
    private Boolean isActive;
    private String avatarUrl;
    
    // Student details (only populated for STUDENT role)
    private String studentCode;
   
}

