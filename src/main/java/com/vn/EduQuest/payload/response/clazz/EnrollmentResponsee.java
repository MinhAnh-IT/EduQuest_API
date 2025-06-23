package com.vn.EduQuest.payload.response.clazz;

import com.vn.EduQuest.enums.EnrollmentStatus;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class EnrollmentResponsee {
    Long enrollmentId;
    String classId;
    Long studentId;
    EnrollmentStatus status;
    String studentName;
}
