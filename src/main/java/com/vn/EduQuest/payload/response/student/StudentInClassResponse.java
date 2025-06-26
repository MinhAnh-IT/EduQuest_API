package com.vn.EduQuest.payload.response.student;

import java.time.LocalDateTime;

import com.vn.EduQuest.enums.EnrollmentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentInClassResponse {
    private Long studentId;
    private String studentCode;
    private String studentName;
    private String studentEmail;
    private String avatarUrl;
    private EnrollmentStatus enrollmentStatus;
    private LocalDateTime enrolledAt;
}
