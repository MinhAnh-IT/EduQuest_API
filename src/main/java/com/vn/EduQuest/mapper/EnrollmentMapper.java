package com.vn.EduQuest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vn.EduQuest.entities.Enrollment;
import com.vn.EduQuest.payload.response.EnrollmentResponse;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {
    
    @Mapping(source = "id", target = "enrollmentId")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.user.name", target = "studentName")
    @Mapping(source = "clazz.id", target = "classId")
    @Mapping(source = "clazz.name", target = "className")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdAt", target = "enrollmentDate")
    @Mapping(target = "message", ignore = true)
    EnrollmentResponse toEnrollmentResponse(Enrollment enrollment);
}
