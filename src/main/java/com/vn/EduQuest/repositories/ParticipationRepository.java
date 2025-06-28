package com.vn.EduQuest.repositories;
import java.util.List;
import java.util.Optional;

import com.vn.EduQuest.enums.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.entities.Participation;
import com.vn.EduQuest.entities.Student;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    Optional<Participation> findByStudent_IdAndExercise_Id(Long studentId, Long exerciseId);
    List<Participation> findByStudent_Id(Long studentId);
    List<Participation> findByExercise_Id(Long exerciseId);
    List<Participation> findByStudent_IdAndExercise_IdIn(Long studentId, List<Long> exerciseIds);
    Optional<Participation> findByStudentAndExercise(Student student, Exercise exercise);

    Integer countByExerciseAndStatus(Exercise exercise, ParticipationStatus participationStatus);
}
