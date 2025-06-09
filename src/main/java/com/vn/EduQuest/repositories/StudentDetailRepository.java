package com.vn.EduQuest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vn.EduQuest.entities.StudentDetail;
 
public interface StudentDetailRepository extends JpaRepository<StudentDetail, Long> {
    boolean existsByStudentCode(String studentCode);
} 