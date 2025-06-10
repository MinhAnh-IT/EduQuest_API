package com.vn.EduQuest.mapper;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.payload.request.UserForGenerateToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "active", ignore = true)
    UserForGenerateToken toUserForGenerateToken(User user);

}