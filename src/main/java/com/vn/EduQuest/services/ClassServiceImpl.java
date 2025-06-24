package com.vn.EduQuest.services;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.EnrollmentStatus;
import com.vn.EduQuest.enums.Role;
import com.vn.EduQuest.mapper.EnrollmentMapper;
import com.vn.EduQuest.payload.request.Class.ClassCreateRequest;
import com.vn.EduQuest.payload.request.Class.EnrollmentApprovalRequest;
import com.vn.EduQuest.payload.response.clazz.ClassCreateResponse;
import com.vn.EduQuest.payload.response.clazz.EnrollmentResponsee;
import com.vn.EduQuest.payload.response.clazz.InstructorClassResponse;
import com.vn.EduQuest.repositories.UserRepository;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import com.vn.EduQuest.entities.Class;
import com.vn.EduQuest.entities.Enrollment;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.ClassMapper;
import com.vn.EduQuest.mapper.StudentMapper;
import com.vn.EduQuest.payload.response.clazz.ClassDetailResponse;
import com.vn.EduQuest.payload.response.student.StudentInClassResponse;
import com.vn.EduQuest.repositories.ClassRepository;
import com.vn.EduQuest.repositories.EnrollmentRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ClassServiceImpl implements ClassService {

    ClassRepository classRepository;
    EnrollmentRepository enrollmentRepository;
    ClassMapper classMapper;
    StudentMapper studentMapper;
    UserRepository userRepository;
    EnrollmentMapper enrollmentMapper;

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
            // Find class by ID
            Class clazz = classRepository.findById(classId)

                    .orElseThrow(() -> new CustomException(StatusCode.CLASS_NOT_FOUND_BY_ID));

            // Get all enrollments for this class
            List<Enrollment> enrollments = enrollmentRepository.findByClazz(clazz);

            // Convert to response DTOs
            return enrollments.stream()
                    .map(studentMapper::toStudentInClassResponse)
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
                .map(classMapper::toInstructorClassResponse)
                .collect(Collectors.toList());
    }

}
