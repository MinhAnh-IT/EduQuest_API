package com.vn.EduQuest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vn.EduQuest.entities.Class;
import com.vn.EduQuest.payload.response.clazz.ClassDetailResponse;

@Mapper(componentModel = "spring")
public interface ClassMapper {
    
    @Mapping(source = "id", target = "classId")
    @Mapping(source = "name", target = "className")
    @Mapping(source = "instructor.name", target = "instructorName")
    @Mapping(source = "instructor.email", target = "instructorEmail")
    @Mapping(target = "studentCount", ignore = true) // Will be set manually
    ClassDetailResponse toClassDetailResponse(Class clazz);
}
