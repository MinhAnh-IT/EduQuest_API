package com.vn.EduQuest.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vn.EduQuest.entities.Participation;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    Optional<Participation> findByStudent_IdAndExercise_Id(Long studentId, Long exerciseId);
    List<Participation> findByStudent_Id(Long studentId);
    List<Participation> findByExercise_Id(Long exerciseId);
    List<Participation> findByStudent_IdAndExercise_IdIn(Long studentId, List<Long> exerciseIds);
}
