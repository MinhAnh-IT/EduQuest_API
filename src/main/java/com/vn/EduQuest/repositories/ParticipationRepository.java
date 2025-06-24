package com.vn.EduQuest.repositories;

import com.vn.EduQuest.entities.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    Optional<Participation> findByStudent_IdAndExercise_Id(Long studentId, Long exerciseId);
    List<Participation> findByStudent_Id(Long studentId);

     List<Participation> findByStudent_IdAndExercise_IdIn(Long studentId, List<Long> exerciseIds);
}
