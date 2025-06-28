package com.vn.EduQuest.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.entities.ExerciseQuestion;
import com.vn.EduQuest.entities.Participation;
import com.vn.EduQuest.entities.SubmissionAnswer;
import com.vn.EduQuest.enums.ParticipationStatus;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.ParticipationMapper;
import com.vn.EduQuest.mapper.ResultMapper;
import com.vn.EduQuest.mapper.SubmissionAnswerMapper;
import com.vn.EduQuest.payload.request.participation.SubmissionAnswerRequest;
import com.vn.EduQuest.payload.request.participation.SubmissionExamRequest;
import com.vn.EduQuest.payload.response.exercise.ExerciseResultsResponse;
import com.vn.EduQuest.payload.response.exercise.StudentResultResponse;
import com.vn.EduQuest.payload.response.QuestionResultDTO;
import com.vn.EduQuest.payload.response.ResultDTO;
import com.vn.EduQuest.payload.response.participation.StartExamResponse;
import com.vn.EduQuest.payload.response.participation.StudentTestDetailResponse;
import com.vn.EduQuest.payload.response.participation.SubmissionAnswerResponse;
import com.vn.EduQuest.payload.response.question.QuestionResponse;
import com.vn.EduQuest.repositories.AnswerRepository;
import com.vn.EduQuest.repositories.ExerciseQuestionRepository;
import com.vn.EduQuest.repositories.ExerciseRepository;
import com.vn.EduQuest.repositories.ParticipationRepository;
import com.vn.EduQuest.repositories.SubmissionAnswerRepository;
import com.vn.EduQuest.utills.GradingService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

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
    public Participation getParticipationById(long participationId) throws CustomException {
        return participationRepository.findById(participationId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "participation", participationId));
    }

    @Override
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

        return resultMapper.toResultDTO(participation, exercise,questionResultDTOS);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ExerciseResultsResponse getExerciseResults(Long instructorId, Long exerciseId) throws CustomException {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new CustomException(StatusCode.EXERCISE_NOT_FOUND, exerciseId));

        if (!exercise.getInstructor().getId().equals(instructorId)) {
            throw new CustomException(StatusCode.FORBIDDEN);
        }

        List<Participation> participations = participationRepository.findByExercise_Id(exerciseId);

        int totalQuestions = exerciseService.getTotalQuestionsByExerciseId(exerciseId);

        List<StudentResultResponse> studentResults = participations.stream()
                .map(participation -> {
                    StudentResultResponse result = new StudentResultResponse();
                    result.setParticipationId(participation.getId());
                    result.setStudentName(participation.getStudent().getUser().getName());
                    result.setStudentCode(participation.getStudent().getStudentCode());
                    result.setStudentEmail(participation.getStudent().getUser().getEmail());
                    result.setScore((double) participation.getScore()); // Convert float to Double
                    result.setTotalQuestions(totalQuestions);
                    result.setStatus(participation.getStatus());
                    result.setStartedAt(participation.getStartAt()); // Sử dụng startAt thay vì createdAt
                    result.setSubmittedAt(participation.getSubmittedAt());

                    // Tính correctAnswers nếu đã submit - tính số câu đúng thực tế
                    if (participation.getStatus() == ParticipationStatus.SUBMITTED) {
                        // Lấy tất cả submission answers của participation này
                        List<SubmissionAnswer> submissionAnswers = submissionAnswerRepository.findByParticipation_Id(participation.getId());
                        int correctCount = 0;
                        
                        for (SubmissionAnswer submissionAnswer : submissionAnswers) {
                            // Kiểm tra xem answer được chọn có đúng không
                            if (submissionAnswer.getAnswer() != null && submissionAnswer.getAnswer().getIsCorrect()) {
                                correctCount++;
                            }
                        }
                        result.setCorrectAnswers(correctCount);
                    }

                    // Tính duration nếu đã submit
                    if (participation.getSubmittedAt() != null) {
                        long minutes = java.time.Duration.between(participation.getStartAt(), participation.getSubmittedAt()).toMinutes();
                        result.setDuration(minutes + " minutes");
                    }

                    return result;
                })
                .collect(Collectors.toList());

        // Tạo response
        ExerciseResultsResponse response = new ExerciseResultsResponse();
        response.setExerciseId(exerciseId);
        response.setExerciseName(exercise.getName());
        response.setTotalQuestions(totalQuestions);
        response.setTotalParticipants(participations.size());
        response.setStudentResults(studentResults);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentTestDetailResponse getStudentTestDetail(Long participationId) throws CustomException {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new CustomException(StatusCode.PARTICIPATION_NOT_FOUND, participationId));
        
        // Lấy các câu hỏi của bài kiểm tra
        List<ExerciseQuestion> exerciseQuestions = exerciseQuestionRepository.findByExercise_Id(participation.getExercise().getId());
        // Lấy các đáp án sinh viên đã chọn
        List<SubmissionAnswer> submissionAnswers = submissionAnswerRepository.findByParticipation_Id(participationId);
        
        // Map: questionId -> selectedAnswerId
        java.util.Map<Long, Long> selectedMap = submissionAnswers.stream()
                .collect(Collectors.toMap(
                        sa -> sa.getExerciseQuestion().getQuestion().getId(),
                        sa -> sa.getAnswer() != null ? sa.getAnswer().getId() : null
                ));
        
        // Tính số câu đúng
        int correctAnswers = 0;
        List<QuestionResultDTO> questionResults = new ArrayList<>();
        for (ExerciseQuestion eq : exerciseQuestions) {
            Long questionId = eq.getQuestion().getId();
            // Lấy đáp án đúng
            Long correctAnswerId = questionService.getCorrectAnswerId(questionId);
            // Lấy đáp án sinh viên chọn
            Long selectedAnswerId = selectedMap.get(questionId);
            if (selectedAnswerId != null && selectedAnswerId.equals(correctAnswerId)) {
                correctAnswers++;
            }
            // Lấy thông tin câu hỏi (bao gồm danh sách đáp án)
            QuestionResponse questionResponse = questionService.getQuestionResponseById(questionId);
            questionResults.add(QuestionResultDTO.builder()
                    .selectedAnswer(selectedAnswerId)
                    .correctAnswer(correctAnswerId)
                    .question(questionResponse)
                    .build());
        }
        // Tính thời gian làm bài (giây)
        Integer duration = null;
        if (participation.getStartAt() != null && participation.getSubmittedAt() != null) {
            duration = (int) java.time.Duration.between(participation.getStartAt(), participation.getSubmittedAt()).getSeconds();
        }
        return com.vn.EduQuest.payload.response.participation.StudentTestDetailResponse.builder()
                .participationId(participation.getId())
                .exerciseId(participation.getExercise().getId())
                .studentName(participation.getStudent().getUser().getName())
                .studentCode(participation.getStudent().getStudentCode())
                .studentEmail(participation.getStudent().getUser().getEmail())
                .score((double) participation.getScore())
                .totalQuestions(exerciseQuestions.size())
                .correctAnswers(correctAnswers)
                .status(participation.getStatus().name())
                .startedAt(participation.getStartAt())
                .submittedAt(participation.getSubmittedAt())
                .duration(duration)
                .questions(questionResults)
                .build();
    }
}