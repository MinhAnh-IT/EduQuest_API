package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.SubmissionAnswer;
import com.vn.EduQuest.exceptions.CustomException;

import java.util.List;

public interface SubmissionAnswerService {
    void saveAllSubmissionAnswer(List<SubmissionAnswer> submissionAnswer) throws CustomException;
    List<SubmissionAnswer> getSubmissionAnswersByParticipationId(Long participationId) throws CustomException;
}
