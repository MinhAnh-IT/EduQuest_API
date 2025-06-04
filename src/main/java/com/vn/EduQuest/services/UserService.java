package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.User;

public interface UserService {
    User findByEmail(String email);
    void updatePassword(User user, String newPassword);
    User findByUsername(String username);
}
