package com.vn.EduQuest.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.experimental.FieldDefaults;
import lombok.Data;
import lombok.AccessLevel;
import java.time.LocalDate;
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "students")
public class StudentDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;
    @Column(name = "student_code", nullable = false)
    String studentCode;
    @Column(name = "faculty", nullable = false)
    String faculty;
    @Column(name = "enrolled_year", nullable = false)
    int enrolledYear;
    @Column(name = "birth_date", nullable = false)
    LocalDate birthDate;
}
