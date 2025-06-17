package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImp implements UserService{
    UserRepository userRepository;

    @Override
    public boolean isUserExist(long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public boolean isStudent(long userId) throws CustomException {
        return userRepository.findById(userId)
                .map(User::isStudent)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "user", userId));
    }

    @Override
    public User getUserById(long userId) throws CustomException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "user", userId));
    }
}
