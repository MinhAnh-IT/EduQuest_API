package com.vn.EduQuest.repositories;

import com.vn.EduQuest.entities.Answer;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Query("SELECT a.id FROM Answer a WHERE a.question.id = :questionId AND a.isCorrect = true")
    Long findCorrectAnswerIdByQuestionId(@Param("questionId") Long questionId);

    List<Answer> findByQuestionId(Long questionId);

}
