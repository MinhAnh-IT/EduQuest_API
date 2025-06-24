package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.entities.Participation;
import com.vn.EduQuest.entities.Student;
import com.vn.EduQuest.enums.ParticipationStatus;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.ExerciseMapper;
import com.vn.EduQuest.mapper.ExerciseQuestionMapper;
import com.vn.EduQuest.payload.response.Exercise.ExerciseResponse;
import com.vn.EduQuest.payload.response.exerciseQuestion.ExerciseQuestionResponse;
import com.vn.EduQuest.repositories.ExerciseQuestionRepository;
import com.vn.EduQuest.repositories.ExerciseRepository;
import com.vn.EduQuest.repositories.ParticipationRepository;
import com.vn.EduQuest.repositories.StudentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseServiceImpl implements ExerciseService {
    ExerciseRepository exerciseRepository;
    ExerciseQuestionRepository exerciseQuestionRepository;
    ExerciseQuestionMapper exerciseQuestionMapper;
    StudentRepository studentRepository;
    ParticipationRepository participationRepository;
    ExerciseMapper exerciseMapper;

    @Override
    public List<ExerciseQuestionResponse> getQuestionsByExerciseId(long exerciseId) throws CustomException {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "Exercise", exerciseId));

        var questions = exerciseQuestionRepository.findQuestionsWithIdsByExercise(exercise);
        return questions.stream()
                .map(question ->
                        exerciseQuestionMapper.toExerciseQuestionResponse(
                                question.getQuestion(), question.getEqId()))
                .collect(Collectors.toList());

    }

    @Override
    public boolean isExerciseNotExist(long exerciseId) {
        return !exerciseRepository.existsById(exerciseId);
    }

    @Override
    public Exercise getExerciseById(long exerciseId) throws CustomException {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "exercise", exerciseId));
    }

    @Override
    public int getTotalQuestionsByExerciseId(long exerciseId) throws CustomException {
        if (isExerciseNotExist(exerciseId)) {
            throw new CustomException(StatusCode.NOT_FOUND, "exercise", exerciseId);
        }
        return exerciseQuestionRepository.countByExerciseId(exerciseId);
    }

    public List<ExerciseResponse> getExercisesForStudent(Long userId, Long classId) throws CustomException {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "student", userId));
        List<Exercise> eligibleExercises = exerciseRepository.findExercisesByStudentIdAndClassId(student.getId(), classId);
        List<Long> exerciseIds = eligibleExercises.stream()
                .map(Exercise::getId)
                .toList();
        List<Participation> participations = participationRepository.findByStudent_IdAndExercise_IdIn(student.getId(), exerciseIds);
        Map<Long, Participation> participationMap = participations.stream()
                .collect(Collectors.toMap(
                        p -> p.getExercise().getId(),
                        Function.identity(),
                        (p1, p2) -> {
                            LocalDateTime t1 = p1.getSubmittedAt() != null ? p1.getSubmittedAt() : p1.getCreatedAt();
                            LocalDateTime t2 = p2.getSubmittedAt() != null ? p2.getSubmittedAt() : p2.getCreatedAt();
                            return t1.isAfter(t2) ? p1 : p2;
                        }
                ));
        return eligibleExercises.stream().map(exercise -> {
            ExerciseResponse response = exerciseMapper.toResponse(exercise);
            Participation participation = participationMap.get(exercise.getId());
            if (participation != null) {
                response.setStatus(participation.getStatus().toString());
            } else if (LocalDateTime.now().isAfter(exercise.getEndAt())) {
                response.setStatus("EXPIRED");
            } else {
                response.setStatus(null);
            }
            int questionCount = exerciseQuestionRepository.countByExercise_Id(exercise.getId());
            response.setQuestionCount(questionCount);

            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean isExpired(Long exerciseId) throws CustomException {
        Exercise exercise = getExerciseById(exerciseId);
        return exercise.getEndAt().isBefore(java.time.LocalDateTime.now());
    }

}
