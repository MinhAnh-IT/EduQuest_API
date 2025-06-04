package com.vn.EduQuest.services;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.exceptions.UserNotFoundException;
import com.vn.EduQuest.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis())); // cập nhật thời gian

        // Detailed logging for debugging
        System.out.println("New password: " + newPassword);
        System.out.println("Encoded password: " + encodedPassword);
        System.out.println("User before save: " + user);

        userRepository.save(user);

        System.out.println("User after save: " + user);
    }
}