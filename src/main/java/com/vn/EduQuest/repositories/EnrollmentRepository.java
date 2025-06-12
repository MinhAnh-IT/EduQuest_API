package com.vn.EduQuest.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.EduQuest.entities.Class;
import com.vn.EduQuest.entities.Enrollment;
import com.vn.EduQuest.entities.Student;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentAndClazz(Student student, Class clazz);
}
