package com.vn.EduQuest.mapper;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.payload.request.RegisterRequest;
import com.vn.EduQuest.payload.response.RegisterRespone;
import com.vn.EduQuest.payload.response.StudentDetailResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "studentDetail", ignore = true)
    User toEntity(RegisterRequest request);

    RegisterRespone toUserDTO(User user);

    // Map to StudentDetailResponse for student details
    @Mapping(target = "studentCode", source = "studentDetail.studentCode")
    @Mapping(target = "faculty", source = "studentDetail.faculty")
    @Mapping(target = "enrolledYear", source = "studentDetail.enrolledYear")
    @Mapping(target = "birthDate", source = "studentDetail.birthDate")
    StudentDetailResponse toStudentDetailResponse(User user);
}
