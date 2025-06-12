package com.vn.EduQuest.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response class for class code validation
 * Contains only the class information without null student fields
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassValidationResponse {
    private Long classId;
    private String className;
    private String classCode;
    private String instructorName;
    private Integer enrollmentCount;
    private String message;
}
