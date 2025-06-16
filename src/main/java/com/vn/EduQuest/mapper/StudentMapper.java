package com.vn.EduQuest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vn.EduQuest.entities.Student;
import com.vn.EduQuest.payload.request.StudentDetailRequest;

@Mapper(componentModel = "spring")
public interface StudentMapper {
      // Dich Ten Nguon
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Student toEntity (StudentDetailRequest studentDetailRequest);
}
