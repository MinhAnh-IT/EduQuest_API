package com.vn.EduQuest.repositories;

import com.vn.EduQuest.entities.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;

import com.vn.EduQuest.entities.Discussion;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
}
