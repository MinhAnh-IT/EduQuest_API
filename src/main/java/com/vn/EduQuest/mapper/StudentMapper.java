package com.vn.EduQuest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vn.EduQuest.entities.Enrollment;
import com.vn.EduQuest.entities.Student;
import com.vn.EduQuest.payload.request.student.StudentDetailRequest;
import com.vn.EduQuest.payload.response.student.StudentInClassResponse;

@Mapper(componentModel = "spring")
public interface StudentMapper {
      // Dich Ten Nguon
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Student toEntity (StudentDetailRequest studentDetailRequest);
      @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.studentCode", target = "studentCode")
    @Mapping(source = "student.user.name", target = "studentName")
    @Mapping(source = "student.user.email", target = "studentEmail")
    @Mapping(source = "student.user.avatarUrl", target = "avatarUrl")
    @Mapping(source = "status", target = "enrollmentStatus")
    StudentInClassResponse toStudentInClassResponse(Enrollment enrollment);
}
