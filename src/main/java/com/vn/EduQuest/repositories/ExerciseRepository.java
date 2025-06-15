package com.vn.EduQuest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vn.EduQuest.entities.Exercise;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
