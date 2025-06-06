package com.vn.EduQuest.services;

import ch.qos.logback.core.net.SMTPAppenderBase;
import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.Role;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.UserMapper;
import com.vn.EduQuest.payload.request.RegisterRequest;
import com.vn.EduQuest.payload.response.LoginResponse;
import com.vn.EduQuest.payload.response.RegisterRespone;
import com.vn.EduQuest.repositories.UserRepository;
import com.vn.EduQuest.utills.Bcrypt;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    UserMapper userMapper;
    @Override
    public LoginResponse login(String username, String password) {

    return null;
    }

    @Override
    public RegisterRespone register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            try {
                throw new CustomException(StatusCode.EXIST_USERNAME, request.getUsername());
            } catch (CustomException e) {
                throw new RuntimeException(e);
            }
        }
        User user = userMapper.toEntity(request);
        user.setIsActive(true);
        user.setPassword(Bcrypt.hashPassword(request.getPassword()));
        log.info("is teacher {}", request.getIsTeacher());
        if (request.getIsTeacher()){
            user.setRole(Role.INSTRUCTOR);
        }else{
            user.setRole(Role.STUDENT);
        }
        userRepository.save(user);
        return userMapper.toUserDTO(user);
    }
}