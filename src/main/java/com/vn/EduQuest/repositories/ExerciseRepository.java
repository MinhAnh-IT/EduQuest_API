package com.vn.EduQuest.repositories;
import com.vn.EduQuest.entities.Exercise;
import org.springframework.data.repository.query.Param; 
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByClazzIdOrderByStartAtDesc(Long classId);

    @Query("""
        SELECT e FROM Exercise e
        WHERE e.clazz.id = :classId
          AND EXISTS (
            SELECT 1 FROM Enrollment en
            WHERE en.clazz.id = :classId
              AND en.student.id = :studentId
          )
        ORDER BY e.startAt DESC
    """)
    List<Exercise> findExercisesByStudentIdAndClassId(@Param("studentId") Long studentId, @Param("classId") Long classId);
}