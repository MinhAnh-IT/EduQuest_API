package com.vn.EduQuest.payload.request.student;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinClassRequest {
    @NotBlank(message = "Class code is required")
    private String classCode;
    
    // Optional: Provided only when needed for non-authenticated requests
    // For authenticated requests, the student is derived from the current user
    private Long studentId;
}
