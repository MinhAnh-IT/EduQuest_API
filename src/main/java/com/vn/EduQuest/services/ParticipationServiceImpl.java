package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.*;
import com.vn.EduQuest.enums.ParticipationStatus;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.ParticipationMapper;
import com.vn.EduQuest.mapper.ResultMapper;
import com.vn.EduQuest.mapper.SubmissionAnswerMapper;
import com.vn.EduQuest.payload.request.participation.SubmissionAnswerRequest;
import com.vn.EduQuest.payload.request.participation.SubmissionExamRequest;
import com.vn.EduQuest.payload.response.QuestionResultDTO;
import com.vn.EduQuest.payload.response.ResultDTO;
import com.vn.EduQuest.payload.response.participation.StartExamResponse;
import com.vn.EduQuest.payload.response.participation.SubmissionAnswerResponse;
import com.vn.EduQuest.payload.response.question.QuestionResponse;
import com.vn.EduQuest.repositories.*;
import com.vn.EduQuest.utills.GradingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
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
    ExerciseRepository exerciseRepository;
    SubmissionAnswerRepository submissionAnswerRepository;
    ExerciseQuestionRepository exerciseQuestionRepository;
    AnswerRepository answerRepository;
    QuestionService questionService;
    ResultMapper resultMapper;

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
        if (!exerciseService.isExerciseAvailable(exercise) || exerciseService.isExpired(exerciseId)){
            throw new CustomException(StatusCode.EXERCISE_NOT_AVAILABLE);
        }

        var isParticipationExist = participationRepository.findByStudentAndExercise(user.getStudentDetail(), exercise);
        Participation participation;
        if (isParticipationExist.isPresent()) {
            participation = isParticipationExist.get();
            if (participation.getStatus() != ParticipationStatus.IN_PROGRESS) {
                throw new CustomException(StatusCode.PARTICIPATION_NOT_IN_PROGRESS);
            }
        }else {
            participation = participationRepository.save(participationMapper.toEntity(exercise, user));
        }
        var questions = exerciseService.getQuestionsByExerciseId(exerciseId);
        return participationMapper.toResponseAfterStartExam(participation, exercise, questions);
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
    public Optional<Participation> findParticipationExistByStudentAndExercise(Student student, Exercise exercise) throws CustomException {
        return participationRepository.findByStudentAndExercise(student, exercise);
    }

    @Override
    public Participation getParticipationById(long participationId) throws CustomException {
        return participationRepository.findById(participationId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "participation", participationId));
    }

    @Transactional(readOnly = true)
    public ResultDTO getResult(Long studentId, Long exerciseId) throws CustomException {
        Participation participation = participationRepository.findByStudent_IdAndExercise_Id(studentId, exerciseId)
                .orElseThrow(() -> new CustomException(StatusCode.PARTICIPATION_NOT_FOUND, studentId, exerciseId));
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new CustomException(StatusCode.EXERCISE_NOT_FOUND, exerciseId));

        List<ExerciseQuestion> exerciseQuestions = exerciseQuestionRepository.findByExercise_Id(exerciseId);
        List<QuestionResultDTO> questionResultDTOS = new ArrayList<>();
        for (ExerciseQuestion exerciseQuestion : exerciseQuestions) {

            Long selectedAnswerId = submissionAnswerRepository.findSelectedAnswerIdByParticipationIdAndExerciseQuestionId(participation.getId(), exerciseQuestion.getQuestion().getId());

            Long correctAnswerId = answerRepository.findCorrectAnswerIdByQuestionId(exerciseQuestion.getQuestion().getId());

            Long questionId = exerciseQuestion.getQuestion().getId();

            QuestionResponse questionResponse = questionService.getQuestionResponseById(questionId);

            QuestionResultDTO questionResultDTO = QuestionResultDTO.builder()
                    .selectedAnswer(selectedAnswerId)
                    .correctAnswer(correctAnswerId)
                    .question(questionResponse)
                    .build();
            questionResultDTOS.add(questionResultDTO);
        }

        List<SubmissionAnswer> submissionAnswers = submissionAnswerRepository.findByParticipation_Id(participation.getId());


        return resultMapper.toResultDTO(participation, exercise,questionResultDTOS);
    }

}
