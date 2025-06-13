package com.vn.EduQuest.repositories;

import com.vn.EduQuest.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
