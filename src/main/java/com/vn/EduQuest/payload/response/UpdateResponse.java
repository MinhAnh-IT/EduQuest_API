package com.vn.EduQuest.payload.response;


import com.vn.EduQuest.enums.Role;
import lombok.Data;
import lombok.Builder;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder

public class UpdateResponse{
    private Long id;
    private String username;
    private String email;
    private String avatarUrl;
    private String name;
    private Role role;
    Timestamp createdAt;
    private String studentCode; 
}
