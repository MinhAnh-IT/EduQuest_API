package com.vn.EduQuest.repositories;

import com.vn.EduQuest.entities.Class;
import com.vn.EduQuest.entities.User;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import com.vn.EduQuest.entities.Exercise;

import java.util.List;


public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByClazzIdOrderByStartAtDesc(Long classId);


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

    @Query(""" 
                  WHERE e.clazz.id = :classId
                  AND EXISTS (
                    SELECT 1 FROM Enrollment en
                    WHERE en.clazz.id = :classId
                      AND en.student.id = :studentId
                  )
                ORDER BY e.startAt DESC
            """)
    List<Exercise> findExercisesByStudentIdAndClassId(@Param("studentId") Long studentId, @Param("classId") Long classId);

    List<Exercise> findByInstructorOrderByCreatedAtDesc(User teacher);

    List<Exercise> findByClazz(Class clazz);

    @Query("SELECT e FROM Exercise e LEFT JOIN FETCH e.exerciseQuestions WHERE e.id = :id")
    Exercise findWithQuestions(@Param("id") Long id);

}
