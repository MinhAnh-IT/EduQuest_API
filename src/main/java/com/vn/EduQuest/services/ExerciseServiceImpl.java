package com.vn.EduQuest.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.entities.Participation;
import com.vn.EduQuest.entities.Student;
import com.vn.EduQuest.enums.ParticipationStatus;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.ExerciseMapper;
import com.vn.EduQuest.mapper.ExerciseQuestionMapper;
import com.vn.EduQuest.payload.response.Exercise.ExerciseResponse;
import com.vn.EduQuest.payload.response.Exercise.InstructorExerciseResponse;
import com.vn.EduQuest.payload.response.exerciseQuestion.ExerciseQuestionResponse;
import com.vn.EduQuest.repositories.ExerciseQuestionRepository;
import com.vn.EduQuest.repositories.ExerciseRepository;
import com.vn.EduQuest.repositories.ParticipationRepository;
import com.vn.EduQuest.repositories.StudentRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

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

    @Override
    public boolean isExerciseAvailable(Exercise exercise) throws CustomException {
        return !exercise.getStartAt().isAfter(java.time.LocalDateTime.now());
    }

    @Override
    public List<InstructorExerciseResponse> getInstructorExercises(Long instructorId) throws CustomException {
        try {
            // Lấy tất cả exercises của instructor
            List<Exercise> exercises = exerciseRepository.findExercisesByInstructorId(instructorId);
            
            return exercises.stream()
                    .map(exercise -> {
                        InstructorExerciseResponse response = new InstructorExerciseResponse();
                        
                        // Basic exercise info
                        response.setExerciseId(exercise.getId());
                        response.setExerciseName(exercise.getName());
                        response.setStartAt(exercise.getStartAt());
                        response.setEndAt(exercise.getEndAt());
                        response.setDurationMinutes(exercise.getDurationMinutes());
                        response.setCreatedAt(exercise.getCreatedAt());
                        
                        // Determine status
                        LocalDateTime now = LocalDateTime.now();
                        String status;
                        if (exercise.getStartAt().isAfter(now)) {
                            status = "UPCOMING";
                        } else if (exercise.getEndAt().isBefore(now)) {
                            status = "EXPIRED";
                        } else {
                            status = "ACTIVE";
                        }
                        response.setStatus(status);
                        
                        // Get statistics
                        int totalQuestions = 0;
                        try {
                            totalQuestions = getTotalQuestionsByExerciseId(exercise.getId());
                        } catch (CustomException e) {
                            totalQuestions = 0; // Default to 0 if error
                        }
                        response.setTotalQuestions(totalQuestions);
                        
                        // Get participation statistics
                        List<Participation> participations = participationRepository.findByExercise_Id(exercise.getId());
                        response.setTotalParticipants(participations.size());
                        
                        long submittedCount = participations.stream()
                                .filter(p -> p.getStatus() == ParticipationStatus.SUBMITTED)
                                .count();
                        response.setSubmittedCount((int) submittedCount);
                        
                        long inProgressCount = participations.stream()
                                .filter(p -> p.getStatus() == ParticipationStatus.IN_PROGRESS)
                                .count();
                        response.setInProgressCount((int) inProgressCount);
                        
                        // Set class info from exercise entity
                        response.setClassId(exercise.getClassId());
                        // TODO: Get class name if needed from class repository
                        response.setClassName(null);
                        
                        return response;
                    })
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<InstructorExerciseResponse> getInstructorExercisesByClass(Long instructorId, Long classId) throws CustomException {
        try {
            // Verify instructor has access to this class
            // This should be done via ClassService but for now we'll trust the classId
            
            // Lấy exercises của instructor trong lớp cụ thể
            List<Exercise> exercises = exerciseRepository.findExercisesByInstructorIdAndClassId(instructorId, classId);
            
            return exercises.stream()
                    .map(exercise -> {
                        InstructorExerciseResponse response = new InstructorExerciseResponse();
                        
                        // Basic exercise info
                        response.setExerciseId(exercise.getId());
                        response.setExerciseName(exercise.getName());
                        response.setStartAt(exercise.getStartAt());
                        response.setEndAt(exercise.getEndAt());
                        response.setDurationMinutes(exercise.getDurationMinutes());
                        response.setCreatedAt(exercise.getCreatedAt());
                        
                        // Determine status
                        LocalDateTime now = LocalDateTime.now();
                        String status;
                        if (exercise.getStartAt().isAfter(now)) {
                            status = "UPCOMING";
                        } else if (exercise.getEndAt().isBefore(now)) {
                            status = "EXPIRED";
                        } else {
                            status = "ACTIVE";
                        }
                        response.setStatus(status);
                        
                        // Get statistics
                        int totalQuestions = 0;
                        try {
                            totalQuestions = getTotalQuestionsByExerciseId(exercise.getId());
                        } catch (CustomException e) {
                            totalQuestions = 0; // Default to 0 if error
                        }
                        response.setTotalQuestions(totalQuestions);
                        
                        // Get participation statistics
                        List<Participation> participations = participationRepository.findByExercise_Id(exercise.getId());
                        response.setTotalParticipants(participations.size());
                        
                        long submittedCount = participations.stream()
                                .filter(p -> p.getStatus() == ParticipationStatus.SUBMITTED)
                                .count();
                        response.setSubmittedCount((int) submittedCount);
                        
                        long inProgressCount = participations.stream()
                                .filter(p -> p.getStatus() == ParticipationStatus.IN_PROGRESS)
                                .count();
                        response.setInProgressCount((int) inProgressCount);
                        
                        // Set class info since we know the classId
                        response.setClassId(classId);
                        // TODO: Get class name if needed from class repository
                        response.setClassName(null);
                        
                        return response;
                    })
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

}
