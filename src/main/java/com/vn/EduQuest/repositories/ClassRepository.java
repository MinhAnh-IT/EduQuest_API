package com.vn.EduQuest.repositories;

import java.util.List;
import java.util.Optional;

import com.vn.EduQuest.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.EduQuest.entities.Class;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    Optional<Class> findByClassCode(String classCode);
    boolean existsByClassCode(String classCode);
    List<Class> findByInstructorId(Long instructorId);

    List<Class> findByInstructor(User teacher);
}

