package com.vn.EduQuest.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.vn.EduQuest.enums.EnrollmentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Entity
@Table(name = "enrollments")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "class_id")
    Class clazz;    @ManyToOne
    @JoinColumn(name = "student_id")
    Student student;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    EnrollmentStatus status;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;
}