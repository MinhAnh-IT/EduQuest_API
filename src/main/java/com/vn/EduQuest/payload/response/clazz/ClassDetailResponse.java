package com.vn.EduQuest.payload.response.clazz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassDetailResponse {
    private Long classId;
    private String className;
    private String classCode;
    private Integer studentCount;
    private String instructorName;
    private String instructorEmail;
}
