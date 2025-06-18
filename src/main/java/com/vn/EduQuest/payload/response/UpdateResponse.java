package com.vn.EduQuest.payload.response;


import com.vn.EduQuest.enums.Role;
import lombok.Data;
import lombok.Builder;

@Data
@Builder

public class UpdateResponse{
    private Long id;
    private String username;
    private String email;
    private String avatarUrl;
    private String name;
    private Role role;
    private String studentCode; 
}
