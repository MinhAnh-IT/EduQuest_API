package com.vn.EduQuest.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vn.EduQuest.entities.Exercise;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    @Query("""
        SELECT e FROM Exercise e
        WHERE e.classId = :classId
        AND EXISTS (
            SELECT 1 FROM Enrollment en WHERE en.clazz.id = :classId AND en.student.id = :studentId
        )
        ORDER BY e.startAt DESC
    """)
    List<Exercise> findExercisesByStudentIdAndClassId(@Param("studentId") Long studentId, @Param("classId") Long classId);
    
    // Lấy tất cả bài tập của instructor
    @Query("""
        SELECT e FROM Exercise e
        WHERE e.instructor.id = :instructorId
        ORDER BY e.createdAt DESC
    """)
    List<Exercise> findExercisesByInstructorId(@Param("instructorId") Long instructorId);
    
    // Lấy bài tập của instructor theo lớp
    @Query("""
        SELECT e FROM Exercise e
        WHERE e.instructor.id = :instructorId
        AND e.classId = :classId
        ORDER BY e.createdAt DESC
    """)
    List<Exercise> findExercisesByInstructorIdAndClassId(@Param("instructorId") Long instructorId, @Param("classId") Long classId);
}
