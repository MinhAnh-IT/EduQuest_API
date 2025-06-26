package com.vn.EduQuest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vn.EduQuest.entities.Enrollment;
import com.vn.EduQuest.payload.response.clazz.EnrollmentResponsee;
import com.vn.EduQuest.payload.response.enrollment.EnrollmentResponse;
import com.vn.EduQuest.payload.response.enrollment.PendingEnrollmentResponse;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {    @Mapping(source = "id", target = "enrollmentId")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "clazz.id", target = "classId")
    @Mapping(source = "clazz.name", target = "className")
    @Mapping(source = "clazz.instructor.name", target = "instructorName")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdAt", target = "enrolledAt")
    EnrollmentResponse toEnrollmentResponse(Enrollment enrollment);

    @Mapping(source = "enrollment.id", target = "enrollmentId")
    @Mapping(source = "clazz.id", target = "classId")
    @Mapping(source = "student.user.name", target = "studentName")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "status", target = "status")
    EnrollmentResponsee toApproveResponse(Enrollment enrollment);

    @Mapping(source = "enrollment.id", target = "enrollmentId")
    @Mapping(source = "enrollment.clazz.id", target = "classId")
    @Mapping(source = "student.user.name", target = "studentName")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.studentCode", target = "studentCode")
    PendingEnrollmentResponse toPendingResponse(Enrollment enrollment);
}
