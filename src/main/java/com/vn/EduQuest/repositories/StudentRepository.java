package com.vn.EduQuest.repositories;

import com.vn.EduQuest.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
     boolean existsByStudentCode(String studentCode);
}
