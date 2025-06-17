package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.*;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.ResultMapper;
import com.vn.EduQuest.payload.response.ResultDTO;
import com.vn.EduQuest.repositories.*;
import com.vn.EduQuest.enums.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ResultServiceImpl implements ResultService {
    ParticipationRepository participationRepository;
    ExerciseRepository exerciseRepository;
    ExerciseQuestionRepository exerciseQuestionRepository;
    SubmissionAnswerRepository submissionAnswerRepository;
    ResultMapper resultMapper;

    @Transactional(readOnly = true)
    public ResultDTO getResult(Long studentId, Long exerciseId) throws CustomException {
        log.info("Fetching result for studentId: {} and exerciseId: {}", studentId, exerciseId);

        Participation participation = participationRepository.findByStudent_IdAndExercise_Id(studentId, exerciseId)
                .orElseThrow(() -> new CustomException(StatusCode.PARTICIPATION_NOT_FOUND, studentId, exerciseId));

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new CustomException(StatusCode.EXERCISE_NOT_FOUND, exerciseId));

        List<ExerciseQuestion> exerciseQuestions = exerciseQuestionRepository.findByExercise_Id(exerciseId);

        List<SubmissionAnswer> submissionAnswers = submissionAnswerRepository.findByParticipation_Id(participation.getId());

        return resultMapper.toResultDTO(participation, exercise, exerciseQuestions, submissionAnswers);
    }

}