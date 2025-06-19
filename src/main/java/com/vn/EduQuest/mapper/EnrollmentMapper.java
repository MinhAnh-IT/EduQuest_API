package com.vn.EduQuest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vn.EduQuest.entities.Enrollment;
import com.vn.EduQuest.payload.response.enrollment.EnrollmentResponse;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {    @Mapping(source = "id", target = "enrollmentId")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "clazz.id", target = "classId")
    @Mapping(source = "clazz.name", target = "className")
    @Mapping(source = "clazz.instructor.name", target = "instructorName")
    @Mapping(source = "status", target = "status")
    EnrollmentResponse toEnrollmentResponse(Enrollment enrollment);
}
