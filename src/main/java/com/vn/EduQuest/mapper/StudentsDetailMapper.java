package com.vn.EduQuest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vn.EduQuest.entities.StudentDetail;
import com.vn.EduQuest.payload.request.StudentDetailRequest;

@Mapper(componentModel = "spring")
public interface StudentsDetailMapper {
    // Dich Ten Nguon
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    StudentDetail toEntity (StudentDetailRequest studentDetailRequest);
}

