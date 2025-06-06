package com.vn.EduQuest.entities;

import java.sql.Timestamp;

import com.vn.EduQuest.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity; // Thêm import này
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "users")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String username;

    @Column(nullable = false)
    String name;

    @Column(unique = true)
    String email;

    @Column(nullable = false)
    String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role;

    @Column(name = "is_active")
    Boolean isActive = true;

    @Column(name = "avatar_url")
    String avatarUrl;

    @Column(name = "last_login_at")
    Timestamp lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    Timestamp updatedAt;
}