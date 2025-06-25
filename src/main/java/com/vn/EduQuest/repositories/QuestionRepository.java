package com.vn.EduQuest.repositories;

import com.vn.EduQuest.entities.Question;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.response.question.QuestionResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByCreatedById(Long instructorId) throws CustomException;

}
