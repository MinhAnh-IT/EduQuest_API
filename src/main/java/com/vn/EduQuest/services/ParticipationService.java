package com.vn.EduQuest.services;

import java.util.Optional;

import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.entities.Participation;
import com.vn.EduQuest.entities.Student;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.participation.SubmissionExamRequest;
import com.vn.EduQuest.payload.response.Exercise.ExerciseResultsResponse;
import com.vn.EduQuest.payload.response.ResultDTO;
import com.vn.EduQuest.payload.response.participation.StartExamResponse;
import com.vn.EduQuest.payload.response.participation.StudentTestDetailResponse;
import com.vn.EduQuest.payload.response.participation.SubmissionAnswerResponse;

public interface ParticipationService {

    StartExamResponse startExam(long exerciseId, long userId) throws Exception;

    SubmissionAnswerResponse submitAnswer(long studentId, SubmissionExamRequest submissionExamRequest) throws CustomException;

    boolean isParticipationExist(long participationId) throws CustomException;

    Optional<Participation> findParticipationExistByStudentAndExercise(Student student, Exercise exercise) throws CustomException;
    Participation getParticipationById(long participationId) throws CustomException;

    ResultDTO getResult(Long studentId, Long exerciseId) throws CustomException;

    ExerciseResultsResponse getExerciseResults(Long instructorId, Long exerciseId) throws CustomException;

    // API chi tiết bài làm kiểm tra của sinh viên
    StudentTestDetailResponse getStudentTestDetail(Long participationId) throws CustomException;
}
