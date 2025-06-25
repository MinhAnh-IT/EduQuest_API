package com.vn.EduQuest.payload.response.enrollment;

import java.time.LocalDateTime;

import com.vn.EduQuest.enums.EnrollmentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Long enrollmentId;
    private Long studentId;
    private Long classId;
    private String className;
    private String instructorName;
    private EnrollmentStatus status;
    private LocalDateTime enrolledAt; // Thời gian vào lớp
}
