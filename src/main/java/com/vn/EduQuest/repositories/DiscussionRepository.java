package com.vn.EduQuest.repositories;

import com.vn.EduQuest.entities.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;

import com.vn.EduQuest.entities.Discussion;

import java.util.List;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
    List<Discussion> findByExercise_Id(Long id);
}
