package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Participation;
import com.vn.EduQuest.entities.SubmissionAnswer;
import com.vn.EduQuest.enums.ParticipationStatus;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.ParticipationMapper;
import com.vn.EduQuest.mapper.SubmissionAnswerMapper;
import com.vn.EduQuest.payload.request.participation.SubmissionAnswerRequest;
import com.vn.EduQuest.payload.request.participation.SubmissionExamRequest;
import com.vn.EduQuest.payload.response.participation.StartExamResponse;
import com.vn.EduQuest.payload.response.participation.SubmissionAnswerResponse;
import com.vn.EduQuest.repositories.ParticipationRepository;
import com.vn.EduQuest.utills.GradingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ParticipationServiceImpl implements ParticipationService{
    ExerciseService exerciseService;
    UserService userService;
    ParticipationMapper participationMapper;
    ParticipationRepository participationRepository;
    AnswerService answerService;
    ExerciseQuestionService exerciseQuestionService;
    SubmissionAnswerService submissionAnswerService;
    GradingService gradingService;
    SubmissionAnswerMapper submissionAnswerMapper;

    @Override
    public StartExamResponse startExam(long exerciseId, long userId) throws Exception {
        if (!userService.isUserExist(userId)) {
            throw new CustomException(StatusCode.NOT_FOUND, "student", userId);
        }
        if (exerciseService.isExerciseNotExist(exerciseId)) {
            throw new CustomException(StatusCode.NOT_FOUND, "exercise", exerciseId);
        }

        var user = userService.getUserById(userId);
        var exercise = exerciseService.getExerciseById(exerciseId);
        var participation = participationMapper.toEntity(exercise, user);

        var participationSaved = participationRepository.save(participation);
        var questions = exerciseService.getQuestionsByExerciseId(exerciseId);

        return participationMapper.toResponseAfterStartExam(participationSaved, exercise, questions);
    }

    @Override
    public SubmissionAnswerResponse submitAnswer(long studentId, SubmissionExamRequest submissionExamRequest) throws CustomException {
        if (!userService.isUserExist(studentId) || !userService.isStudent(studentId)) {
            throw new CustomException(StatusCode.NOT_FOUND, "student", studentId);
        }

        Participation participation = getParticipationById(submissionExamRequest.getParticipationId());
        if (participation.getStudent().getUser().getId() != studentId) {
            throw new CustomException(StatusCode.NOT_MATCH, "student", "participation");
        }
        if (participation.getStatus() != ParticipationStatus.IN_PROGRESS) {
            throw new CustomException(StatusCode.PARTICIPATION_NOT_IN_PROGRESS);
        }

        List<SubmissionAnswer> submissionAnswerList = new ArrayList<>();
        for (SubmissionAnswerRequest answerReq : submissionExamRequest.getSelectedAnswers()) {
            var answer = answerService.getAnswerById(answerReq.getSelectedAnswerId());
            var exerciseQuestion = exerciseQuestionService.getExerciseQuestionById(answerReq.getExerciseQuestionId());
            SubmissionAnswer submissionAnswer = submissionAnswerMapper.toEntity(exerciseQuestion, answer, participation);
            submissionAnswerList.add(submissionAnswer);
        }

        submissionAnswerService.saveAllSubmissionAnswer(submissionAnswerList);
        participation.setStatus(ParticipationStatus.SUBMITTED);
        participation.setSubmittedAt(LocalDateTime.now());

        // get total questions from exercise
        int totalQuestions = exerciseService.getTotalQuestionsByExerciseId(participation.getExercise().getId());

        // set score for participation
        participation.setScore(gradingService.autoGrade(submissionAnswerList, totalQuestions));
        participationRepository.save(participation);
        return participationMapper.toSubmissionAnswerResponse(participation);
    }


    @Override
    public boolean isParticipationExist(long participationId) throws CustomException {
        return participationRepository.existsById(participationId);
    }

    @Override
    public Participation getParticipationById(long participationId) throws CustomException {
        return participationRepository.findById(participationId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "participation", participationId));
    }


}
