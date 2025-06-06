package com.vn.EduQuest.mapper;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.Role;
import com.vn.EduQuest.payload.request.RegisterRequest;
import com.vn.EduQuest.payload.response.RegisterRespone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = Role.class)

public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    User toEntity(RegisterRequest request);

    RegisterRespone toUserDTO(User user);
}
