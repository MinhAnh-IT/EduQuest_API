package com.vn.EduQuest.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vn.EduQuest.entities.Class;
import com.vn.EduQuest.entities.Enrollment;
import com.vn.EduQuest.entities.Student;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentAndClazz(Student student, Class clazz);
    boolean existsByStudentAndClazz(Student student, Class clazz);
    long countByClazz(Class clazz);
    
    // Find all enrollments by class
    List<Enrollment> findByClazz(Class clazz);
    
    // Find all enrollments for a specific student, ordered by enrollment date desc
    @Query("SELECT e FROM Enrollment e WHERE e.student = :student ORDER BY e.createdAt DESC")
    List<Enrollment> findByStudentOrderByCreatedAtDesc(@Param("student") Student student);
    
    // Find only ENROLLED enrollments for a specific student, ordered by enrollment date desc
    @Query("SELECT e FROM Enrollment e WHERE e.student = :student AND e.status = :status ORDER BY e.createdAt DESC")
    List<Enrollment> findByStudentAndStatusOrderByCreatedAtDesc(@Param("student") Student student, @Param("status") com.vn.EduQuest.enums.EnrollmentStatus status);
    List<Enrollment>findByClazzAndStatus(Class clazz, com.vn.EduQuest.enums.EnrollmentStatus status);
}
