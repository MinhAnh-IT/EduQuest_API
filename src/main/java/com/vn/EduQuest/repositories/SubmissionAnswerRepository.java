package com.vn.EduQuest.repositories;

import com.vn.EduQuest.entities.SubmissionAnswer;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubmissionAnswerRepository extends JpaRepository<SubmissionAnswer, Long> {
    List<SubmissionAnswer> findByParticipation_Id(Long participationId);
    @Query("SELECT sa.answer.id FROM SubmissionAnswer sa WHERE sa.participation.id = :participationId AND sa.exerciseQuestion.id = :exerciseQuestionId")
    Long findSelectedAnswerIdByParticipationIdAndExerciseQuestionId(
            @Param("participationId") Long participationId,
            @Param("exerciseQuestionId") Long exerciseQuestionId
    );
}
