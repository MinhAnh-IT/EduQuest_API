package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.entities.Participation;
import com.vn.EduQuest.entities.Student;
import com.vn.EduQuest.entities.SubmissionAnswer;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.ExerciseMapper;
import com.vn.EduQuest.mapper.ExerciseQuestionMapper;
import com.vn.EduQuest.payload.request.exercise.ExerciseRequest;
import com.vn.EduQuest.payload.response.exercise.ExerciseResponse;
import com.vn.EduQuest.payload.response.exercise.ExerciseScoreExport;
import com.vn.EduQuest.payload.response.exerciseQuestion.ExerciseQuestionResponse;
import com.vn.EduQuest.repositories.ClassRepository;
import com.vn.EduQuest.repositories.EnrollmentRepository;
import com.vn.EduQuest.repositories.ExerciseQuestionRepository;
import com.vn.EduQuest.repositories.ExerciseRepository;

import com.vn.EduQuest.repositories.ParticipationRepository;
import com.vn.EduQuest.entities.Class;
import com.vn.EduQuest.repositories.StudentRepository;
import com.vn.EduQuest.repositories.SubmissionAnswerRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vn.EduQuest.utills.EmailService;
import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.ParticipationStatus;
import com.vn.EduQuest.enums.Role;
import com.vn.EduQuest.payload.response.exercise.InstructorExerciseResponse;
import com.vn.EduQuest.mapper.QuestionMapper;
import com.vn.EduQuest.payload.response.exercise.ExerciseCreatedResponse;
import com.vn.EduQuest.payload.response.exercise.ExerciseDetailForTeacher;
import com.vn.EduQuest.payload.response.exercise.ExerciseSimpleForTeacherResponse;
import com.vn.EduQuest.payload.response.question.QuestionDetailResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Comparator;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseServiceImpl implements ExerciseService {
    ExerciseRepository exerciseRepository;
    ExerciseQuestionRepository exerciseQuestionRepository;
    ExerciseQuestionMapper exerciseQuestionMapper;
    StudentRepository studentRepository;
    ParticipationRepository participationRepository;
    UserService userService;
    ExerciseMapper exerciseMapper;
    EnrollmentRepository enrollmentRepository;
    ClassRepository classRepository;
    ExerciseQuestionService exerciseQuestionService;
    QuestionMapper questionMapper;
    EmailService emailService;
    ClassService classService;
    SubmissionAnswerRepository submissionAnswerRepository;

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

    private String getStatus(Participation p) {
        if (p == null) return "Chưa làm";
        if (p.getStatus() == ParticipationStatus.SUBMITTED) return "Đã nộp";
        if (p.getStatus() == ParticipationStatus.IN_PROGRESS) return "Đang làm";
        return "Chưa nộp";
    }

    @Override
    public ByteArrayInputStream exportStudentScoresToExcel(Long classId, Long exerciseId) throws CustomException {
        classRepository.findById(classId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "class", classId));
        exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "exercise", exerciseId));

        List<Long> studentIds = enrollmentRepository.findStudentIdsByClassId(classId);

        List<Participation> participations = participationRepository.findByExercise_Id(exerciseId);
        Map<Long, Participation> participationMap = participations.stream()
                .collect(Collectors.toMap(p -> p.getStudent().getId(), p -> p));

        int totalQuestions = exerciseQuestionRepository.countByExerciseId(exerciseId);

        List<Student> students = studentRepository.findAllById(studentIds);

        List<ExerciseScoreExport> dtos = students.stream()
                .map(student -> {
                    ExerciseScoreExport dto = new ExerciseScoreExport();
                    dto.setStudentCode(student.getStudentCode());
                    dto.setName(student.getUser().getName());
                    dto.setTotalQuestions(totalQuestions);

                    Participation p = participationMap.get(student.getId());
                    dto.setStatus(getStatus(p));

                    if (p != null && p.getStatus() == ParticipationStatus.SUBMITTED) {
                        dto.setScore(BigDecimal.valueOf(p.getScore()));
                        List<SubmissionAnswer> submissionAnswers = submissionAnswerRepository.findByParticipation_Id(p.getId());
                        int correct = (int) submissionAnswers.stream()
                                .filter(sa -> sa.getAnswer() != null && Boolean.TRUE.equals(sa.getAnswer().getIsCorrect()))
                                .count();
                        dto.setCorrectCount(correct);
                    } else {
                        dto.setScore(null);
                        dto.setCorrectCount(null);
                    }
                    return dto;
                })
                .sorted(Comparator.comparing(ExerciseScoreExport::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Scores");

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            Row header = sheet.createRow(0);
            String[] headers = {"STT", "Mã SV", "Tên Sinh viên", "Điểm", "Số câu đúng/Tổng số câu", "Trạng thái"};
            for (int i = 0; i < headers.length; i++) {
                var cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            int stt = 1;
            for (ExerciseScoreExport dto : dtos) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(stt++);
                row.createCell(1).setCellValue(dto.getStudentCode());
                row.createCell(2).setCellValue(dto.getName());
                row.createCell(3).setCellValue(dto.getScore() != null ? dto.getScore().doubleValue() : 0);
                if (dto.getCorrectCount() != null) {
                    row.createCell(4).setCellValue(dto.getCorrectCount() + "/" + dto.getTotalQuestions());
                } else {
                    row.createCell(4).setCellValue("0/" + dto.getTotalQuestions());
                }
                row.createCell(5).setCellValue(dto.getStatus());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR, "Excel export error " + e.getMessage());
        }
    }

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

                        response.setClassId(exercise.getClazz().getId());
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
                            totalQuestions = 0;
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

                        response.setClassId(classId);
                        response.setClassName(null);

                        return response;
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    public List<ExerciseSimpleForTeacherResponse> getAllExercisesForTeacher(Long userId) throws CustomException {
        User teacher = userService.getUserById(userId);
        if (teacher.getRole() != Role.INSTRUCTOR) {
            throw new CustomException(StatusCode.FORBIDDEN);
        }
        List<Exercise> exercises = exerciseRepository.findByInstructorOrderByCreatedAtDesc(teacher);
        return exercises.stream()
                .map(exerciseMapper::toSimpleForTeacherResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExerciseSimpleForTeacherResponse> getExercisesByClassIdForTeacher(Long userId, Long classId) throws
            CustomException {
        Class clazz = classService.getClassById(classId);
        if (!Objects.equals(clazz.getInstructor().getId(), userId)) {
            throw new CustomException(StatusCode.FORBIDDEN);
        }

        List<Exercise> exercises = exerciseRepository.findByClazz(clazz);
        return exercises.stream()
                .map(exerciseMapper::toSimpleForTeacherResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExerciseDetailForTeacher getExerciseDetailForTeacher(long userId, Long exerciseId) throws
            CustomException {
        User teacher = userService.getUserById(userId);
        if (teacher.getRole() != Role.INSTRUCTOR) {
            throw new CustomException(StatusCode.FORBIDDEN);
        }
        Exercise exercise = getExerciseById(exerciseId);
        var response = exerciseMapper.toDetailResponse(exercise, questionMapper);
        response.setSubmittedStudentCount(participationRepository.countByExerciseAndStatus(exercise, ParticipationStatus.SUBMITTED));
        return response;
    }

    @Override
    public ExerciseCreatedResponse createExercise(long userId, ExerciseRequest exerciseRequest) throws
            CustomException {
        User instructor = userService.getUserById(userId);
        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new CustomException(StatusCode.INVALID_ROLE);
        }
        Class clazz = classService.getClassById(exerciseRequest.getClassId());
        Exercise exercise = exerciseMapper.toEntity(exerciseRequest, instructor, clazz);

        Exercise exerciseSaved = exerciseRepository.save(exercise);
        sendExerciseCreatedNotification(exerciseSaved);
        exerciseQuestionService.saveAllExerciseQuestions(exerciseSaved, exerciseRequest.getQuestionIds());

        Exercise exerciseWithQuestions = exerciseRepository.findWithQuestions(exerciseSaved.getId());

        var response = exerciseMapper.toCreatedResponse(exerciseWithQuestions);

        List<QuestionDetailResponse> questions = exerciseWithQuestions.getExerciseQuestions().stream()
                .map(exerciseQuestion -> questionMapper.toQuestionDetailResponse(exerciseQuestion.getQuestion()))
                .toList();
        response.setQuestions(questions);
        return response;
    }


    private void sendExerciseCreatedNotification(Exercise exercise) throws CustomException {
        List<Student> students = classService.getListOfStudentsInClass(exercise.getClazz().getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        for (Student student : students) {
            HashMap<String, String> data = new HashMap<>();
            data.put("studentName", student.getUser().getName());
            data.put("exerciseName", exercise.getName());
            data.put("className", exercise.getClazz().getName());
            data.put("startAt", exercise.getStartAt().format(formatter));
            data.put("endAt", exercise.getEndAt().format(formatter));
            data.put("durationMinutes", String.valueOf(exercise.getDurationMinutes()));
            emailService.sendExerciseCreatedNotificationAsync(student.getUser().getEmail(), data);
        }
    }

}
