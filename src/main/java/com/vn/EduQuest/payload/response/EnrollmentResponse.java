package com.vn.EduQuest.payload.response;

import java.time.LocalDateTime;

import com.vn.EduQuest.enums.EnrollmentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {    private Long enrollmentId;
    private Long studentId;
    private String studentName;
    private Long classId;
    private String className;
    private EnrollmentStatus status;
    private LocalDateTime enrollmentDate;
    private String message;
}
