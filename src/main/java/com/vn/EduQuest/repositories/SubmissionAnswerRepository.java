package com.vn.EduQuest.repositories;

import com.vn.EduQuest.entities.SubmissionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionAnswerRepository extends JpaRepository<SubmissionAnswer, Long> {
    List<SubmissionAnswer> findByParticipation_Id(Long participationId);
}
