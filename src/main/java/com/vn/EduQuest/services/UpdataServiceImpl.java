package com.vn.EduQuest.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.UserMapper;
import com.vn.EduQuest.payload.request.UpdateRequest;
import com.vn.EduQuest.payload.response.UpdateResponse;
import com.vn.EduQuest.repositories.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UpdataServiceImpl implements UpdateService {
    UserRepository userRepository;
    UserMapper userMapper;
    @Override
    @Transactional
    public UpdateResponse updateProfiles(Long userId , UpdateRequest request) throws CustomException {
        // Lấy thông tin user 
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "User", userId));
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            user.setEmail(request.getEmail());
        }
        // Cập nhật avatar nếu có
        if (request.getAvatarUrl() != null) {
            // Thêm điều kiện này để xử lý chuỗi rỗng thành null
            if (request.getAvatarUrl().isEmpty()) {
                user.setAvatarUrl(null);
            } else {
                user.setAvatarUrl(request.getAvatarUrl());
            }
        }
        User updatedUser = userRepository.save(user);
        // Sử dụng mapper để chuyển đổi sang response
        return userMapper.toUpdateResponse(updatedUser);
    }  

    @Override
    public UpdateResponse getProfile(Long userId) throws CustomException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
        
        return userMapper.toUpdateResponse(user);
    }
}
