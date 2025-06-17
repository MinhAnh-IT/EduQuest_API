package com.vn.EduQuest.payload.response.enrollment;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Long enrollmentId;
    private Long studentId;
    private String studentName;
    private Long classId;
    private String className;
    private LocalDateTime enrollmentDate;
    private String message;
}
