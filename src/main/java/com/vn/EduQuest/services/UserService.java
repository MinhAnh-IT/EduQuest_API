package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.exceptions.CustomException;

public interface UserService {
    boolean isUserExist(long userId);
    boolean isStudent(long userId) throws CustomException;
    User getUserById(long userId) throws CustomException;

}
