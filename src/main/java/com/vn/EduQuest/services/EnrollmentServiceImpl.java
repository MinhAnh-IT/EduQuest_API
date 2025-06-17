package com.vn.EduQuest.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.EduQuest.entities.Class;
import com.vn.EduQuest.entities.Enrollment;
import com.vn.EduQuest.entities.Student;
import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.EnrollmentStatus;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.student.JoinClassRequest;
import com.vn.EduQuest.repositories.ClassRepository;
import com.vn.EduQuest.repositories.EnrollmentRepository;
import com.vn.EduQuest.repositories.StudentRepository;
import com.vn.EduQuest.repositories.UserRepository;
import com.vn.EduQuest.utills.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final ClassRepository classRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    @Transactional
    public boolean joinClass(String authHeader, JoinClassRequest joinClassRequest) throws CustomException {        // Validate that class code is provided
        if (joinClassRequest.getClassCode() == null || joinClassRequest.getClassCode().trim().isEmpty()) {
            throw new CustomException(StatusCode.CLASS_CODE_REQUIRED, "Class code is required");
        }

        // Extract and validate token from Authorization header
        User currentUser = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.validateToken(token)) {
                Long userId = jwtService.getUserIdFromJWT(token);
                currentUser = userRepository.findById(userId).orElse(null);
            }
        }

        // Require authentication for joining classes
        if (currentUser == null) {
            throw new CustomException(StatusCode.AUTHENTICATION_REQUIRED, "You must be logged in to join a class");
        }

        try {
            // Find the student record associated with the user
            Student student = studentRepository.findByUser(currentUser)
                    .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_A_STUDENT,
                    "User is not registered as a student"));

            // Find the class by class code
            Class classToJoin = classRepository.findByClassCode(joinClassRequest.getClassCode())
                    .orElseThrow(() -> new CustomException(StatusCode.CLASS_NOT_FOUND_BY_CODE));

            // Check if the student is already enrolled in the class
            Optional<Enrollment> existingEnrollment = enrollmentRepository.findByStudentAndClazz(student, classToJoin);
            if (existingEnrollment.isPresent()) {
                throw new CustomException(StatusCode.STUDENT_ALREADY_ENROLLED_IN_CLASS);
            }

            // Create new enrollment
            Enrollment newEnrollment = new Enrollment();
            newEnrollment.setStudent(student);
            newEnrollment.setClazz(classToJoin);
            newEnrollment.setStatus(EnrollmentStatus.PENDING);
            enrollmentRepository.save(newEnrollment);
            return true; // Return true on success
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR,
                    "Failed to join class: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean leaveClass(String authHeader, Long classId) throws CustomException {
        // Extract and validate token from Authorization header
        User currentUser = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.validateToken(token)) {
                Long userId = jwtService.getUserIdFromJWT(token);
                currentUser = userRepository.findById(userId).orElse(null);
            }
        }

        if (currentUser == null) {
            throw new CustomException(StatusCode.AUTHENTICATION_REQUIRED, "You must be logged in to leave a class");
        }

        try {
            Student student = studentRepository.findByUser(currentUser)
                    .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_A_STUDENT,
                    "User is not registered as a student"));
            Class classToLeave = classRepository.findById(classId)
                    .orElseThrow(() -> new CustomException(StatusCode.CLASS_NOT_FOUND_BY_ID, "Class not found with ID: " + classId));
            Enrollment enrollment = enrollmentRepository.findByStudentAndClazz(student, classToLeave)
                    .orElseThrow(() -> new CustomException(StatusCode.STUDENT_NOT_ENROLLED_IN_CLASS, "You are not enrolled in this class."));
            enrollmentRepository.delete(enrollment);
            return true;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR,
                    "Failed to leave class: " + e.getMessage());
        }
    }

    @Override
    public boolean validateClassCode(String classCode) throws CustomException {     
        if (classCode == null || classCode.trim().isEmpty()) {
            throw new CustomException(StatusCode.CLASS_CODE_REQUIRED, "Class code is required");
        }

        try {
            classRepository.findByClassCode(classCode)
                    .orElseThrow(() -> new CustomException(StatusCode.CLASS_NOT_FOUND_BY_CODE,
                    "Class not found with code: " + classCode));

            return true; // Return true if class code is valid
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR,
                    "Failed to validate class code: " + e.getMessage());
        }
    }
}
