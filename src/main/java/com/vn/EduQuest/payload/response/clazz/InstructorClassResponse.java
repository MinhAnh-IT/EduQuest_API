package com.vn.EduQuest.payload.response.clazz;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class InstructorClassResponse {
    Long classId;
    String className;
    String classCode;
}
