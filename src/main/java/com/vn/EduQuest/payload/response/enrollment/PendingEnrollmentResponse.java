package com.vn.EduQuest.payload.response.enrollment;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PendingEnrollmentResponse {
    Long enrollmentId;
    Long classId;
    Long studentId;
    String studentName;
}
