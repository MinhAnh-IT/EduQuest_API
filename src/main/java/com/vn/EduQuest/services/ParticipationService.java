package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Participation;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.participation.SubmissionExamRequest;
import com.vn.EduQuest.payload.response.participation.StartExamResponse;
import com.vn.EduQuest.payload.response.participation.SubmissionAnswerResponse;

public interface ParticipationService {
    StartExamResponse startExam(long exerciseId, long userId) throws Exception;
    SubmissionAnswerResponse submitAnswer(long studentId, SubmissionExamRequest submissionExamRequest) throws CustomException;
    boolean isParticipationExist(long participationId) throws CustomException;
    Participation getParticipationById(long participationId) throws CustomException;
}
