package com.vn.EduQuest.payload.request.Class;

import com.vn.EduQuest.enums.EnrollmentStatus;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class EnrollmentApprovalRequest {
    Long enrollmentId;
    EnrollmentStatus status;
}
