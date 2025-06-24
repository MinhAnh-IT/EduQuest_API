package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.entities.Participation;
import com.vn.EduQuest.entities.Student;
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
public class ExerciseServiceImpl implements ExerciseService{
    ExerciseRepository exerciseRepository;
    ExerciseQuestionRepository exerciseQuestionRepository;
    ExerciseQuestionMapper exerciseQuestionMapper;
    StudentRepository studentRepository;
    ExerciseMapper exerciseMapper;
    ParticipationRepository participationRepository;
    
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
    
    @Override
    public List<ExerciseResponse> getExercisesForStudent(Long userId) throws CustomException {
        Student student = studentRepository.findByUserId(userId)
            .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "student", userId));
        List<Exercise> eligibleExercises = exerciseRepository.findExercisesByStudentId(student.getId());
        List<Participation> participations = participationRepository.findByStudent_Id(student.getId());
        Map<Long, Participation> participationMap = participations.stream()
                .collect(Collectors.toMap(p -> p.getExercise().getId(), Function.identity()));

        return eligibleExercises.stream().map(exercise -> {
            ExerciseResponse response = exerciseMapper.toResponse(exercise);
            Participation participation = participationMap.get(exercise.getId());
            if (participation != null) {
                response.setStatus(participation.getStatus().toString()); // "IN_PROGRESS" hoặc "SUBMITTED"
            } else if (LocalDateTime.now().isAfter(exercise.getEndAt())) {
                response.setStatus("EXPIRED"); // Quá hạn mà chưa làm
            } else {
                response.setStatus(null); // Chưa làm, còn hạn, không cần hiển thị gì đặc biệt
            }
            int questionCount = exerciseQuestionRepository.countByExercise_Id(exercise.getId());
            response.setQuestionCount(questionCount);
            return response;
        }).collect(Collectors.toList());
    }
}
