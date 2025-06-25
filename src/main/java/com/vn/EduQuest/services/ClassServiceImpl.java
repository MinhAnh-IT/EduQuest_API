package com.vn.EduQuest.services;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vn.EduQuest.entities.Class;
import com.vn.EduQuest.entities.Enrollment;
import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.EnrollmentStatus;
import com.vn.EduQuest.enums.Role;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.ClassMapper;
import com.vn.EduQuest.mapper.StudentMapper;
import com.vn.EduQuest.payload.request.Class.ClassCreateRequest;
import com.vn.EduQuest.payload.response.clazz.ClassCreateResponse;
import com.vn.EduQuest.payload.response.clazz.ClassDetailResponse;
import com.vn.EduQuest.payload.response.clazz.InstructorClassResponse;
import com.vn.EduQuest.payload.response.student.StudentInClassResponse;
import com.vn.EduQuest.repositories.ClassRepository;
import com.vn.EduQuest.repositories.EnrollmentRepository;
import com.vn.EduQuest.repositories.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentMapper studentMapper;
    private final UserRepository userRepository;
    private final ClassMapper classMapper;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public ClassDetailResponse getClassDetail(Long classId) throws CustomException {
        try {
            // Find class by ID
            Class clazz = classRepository.findById(classId)

                    .orElseThrow(() -> new CustomException(StatusCode.CLASS_NOT_FOUND_BY_ID));

            // Get student count for this class
            long studentCount = enrollmentRepository.countByClazz(clazz);

            // Map to response DTO
            ClassDetailResponse response = classMapper.toClassDetailResponse(clazz);
            response.setStudentCount((int) studentCount);

            return response;

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<StudentInClassResponse> getStudentsInClass(Long classId) throws CustomException {
        try {
            Class clazz = classRepository.findById(classId)
                    .orElseThrow(() -> new CustomException(StatusCode.CLASS_NOT_FOUND_BY_ID));

            List<Enrollment> enrollments = enrollmentRepository.findByClazz(clazz);

            return enrollments.stream()
                    .map(enrollment -> {
                        StudentInClassResponse response = studentMapper.toStudentInClassResponse(enrollment);
                        String avatarUrl = response.getAvatarUrl();

                        // If avatarUrl exists and is a relative path, convert it to a full URL
                        if (avatarUrl != null && !avatarUrl.isEmpty() && !avatarUrl.startsWith("http")) {
                            if (!avatarUrl.startsWith("/")) {
                                avatarUrl = "/" + avatarUrl;
                            }
                            response.setAvatarUrl(baseUrl + avatarUrl);
                        }
                        return response;
                    })
                    .collect(Collectors.toList());

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<StudentInClassResponse> getEnrolledStudentsInClass(Long classId) throws CustomException {
        try {
            Class clazz = classRepository.findById(classId)
                    .orElseThrow(() -> new CustomException(StatusCode.CLASS_NOT_FOUND_BY_ID));

            // Only get enrollments with ENROLLED status
            List<Enrollment> enrollments = enrollmentRepository.findByClazzAndStatus(clazz, EnrollmentStatus.ENROLLED);

            return enrollments.stream()
                    .map(enrollment -> {
                        StudentInClassResponse response = studentMapper.toStudentInClassResponse(enrollment);
                        String avatarUrl = response.getAvatarUrl();

                        // If avatarUrl exists and is a relative path, convert it to a full URL
                        if (avatarUrl != null && !avatarUrl.isEmpty() && !avatarUrl.startsWith("http")) {
                            if (!avatarUrl.startsWith("/")) {
                                avatarUrl = "/" + avatarUrl;
                            }
                            response.setAvatarUrl(baseUrl + avatarUrl);
                        }
                        return response;
                    })
                    .collect(Collectors.toList());

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    static String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    static int CLASS_CODE_LENGTH = 8;

    private String generateUniqueClassCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CLASS_CODE_LENGTH);

        while (true) {
            code.setLength(0);
            for (int i = 0; i < CLASS_CODE_LENGTH; i++) {
                code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            if (!classRepository.existsByClassCode(code.toString())) {
                break;
            }
        }

        return code.toString();
    }

    @Override
    public ClassCreateResponse createClass(Long InstructorID, ClassCreateRequest request) throws CustomException {
        User instructor = userRepository.findById(InstructorID)
                .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new CustomException(StatusCode.INVALID_ROLE);
        }
        String classCode = generateUniqueClassCode();
        Class newClass = classMapper.toCreateEntity(request);
        newClass.setName(request.getClassName());
        newClass.setInstructor(instructor);
        newClass.setClassCode(classCode);
        Class savedClass = classRepository.save(newClass);

        return classMapper.toCreateResponse(savedClass);
    }

    @Override
    public List<InstructorClassResponse> getInstructorClasses(Long instructorId) throws CustomException {
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));

        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new CustomException(StatusCode.INVALID_ROLE);
        }

        List<Class> instructorClasses = classRepository.findByInstructorId(instructorId);
        if (instructorClasses.isEmpty()) {
            throw new CustomException(StatusCode.NOT_FOUND);
        }

        return instructorClasses.stream()
                .map(clazz -> {
                    InstructorClassResponse response = classMapper.toInstructorClassResponse(clazz);
                    Long studentCount = enrollmentRepository.countByClazz(clazz);
                    response.setNumberOfStudents(studentCount);
                    return response;
                })
                .collect(Collectors.toList());
    }
}
