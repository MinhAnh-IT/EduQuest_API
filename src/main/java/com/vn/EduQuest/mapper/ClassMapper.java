package com.vn.EduQuest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vn.EduQuest.entities.Class;
import com.vn.EduQuest.payload.request.Class.ClassCreateRequest;
import com.vn.EduQuest.payload.response.clazz.ClassCreateResponse;
import com.vn.EduQuest.payload.response.clazz.ClassDetailResponse;
import com.vn.EduQuest.payload.response.clazz.InstructorClassResponse;

@Mapper(componentModel = "spring")
public interface ClassMapper {
    @Mapping(source = "id", target = "classId")
    @Mapping(source = "name", target = "className")
    @Mapping(source = "classCode", target = "classCode")
    @Mapping(source = "instructor.name", target = "instructorName")
    @Mapping(source = "instructor.email", target = "instructorEmail")
    @Mapping(target = "studentCount", ignore = true)
    ClassDetailResponse toClassDetailResponse(Class clazz);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "instructor", ignore = true)
    @Mapping(target = "classCode", ignore = true)
    @Mapping(target = "name", source = "request.className")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Class toCreateEntity(ClassCreateRequest request);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "classCode", target = "classCode")
    @Mapping(source = "instructor.id", target = "instructorId")
    ClassCreateResponse toCreateResponse(Class entity);

    @Mapping(source = "clazz.id", target = "classId")
    @Mapping(source = "clazz.classCode", target = "classCode")
    @Mapping(source = "clazz.name", target = "className")
    InstructorClassResponse toInstructorClassResponse(Class clazz);
}
