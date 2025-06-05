package com.vn.EduQuest.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vn.EduQuest.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByEmail(String email);
    Optional<User> findByEmailAndUsername(String email, String username);
    Optional<User> findByUsername(String username);
}