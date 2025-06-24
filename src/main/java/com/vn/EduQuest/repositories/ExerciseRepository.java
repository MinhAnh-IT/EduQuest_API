package com.vn.EduQuest.repositories;
import com.vn.EduQuest.entities.Exercise;
import org.springframework.data.repository.query.Param; 
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    @Query("""
        SELECT e FROM Exercise e
        JOIN ExerciseClass ec ON e.id = ec.exercise.id
        JOIN Enrollment en ON ec.clazz.id = en.clazz.id
        WHERE en.student.id = :studentId
        ORDER BY e.startAt DESC
    """)
    List<Exercise> findExercisesByStudentId(@Param("studentId") Long studentId);
}
