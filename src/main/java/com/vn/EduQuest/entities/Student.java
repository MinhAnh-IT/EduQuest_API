package com.vn.EduQuest.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "students")
@Getter
@Setter
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "student_code", unique = true, nullable = false)
    private String studentCode;

    @Column(name = "faculty")
    private String faculty;

    @Column(name = "class_code")
    private String classCode;

    @Column(name = "enrolled_year")
    private Integer enrolledYear;

    @Column(name = "birth_date")
    private LocalDate birthDate;
}
