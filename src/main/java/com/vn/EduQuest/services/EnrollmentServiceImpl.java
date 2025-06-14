package com.vn.EduQuest.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.EduQuest.entities.Class;
import com.vn.EduQuest.entities.Enrollment;
import com.vn.EduQuest.entities.Student;
import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.JoinClassRequest;
import com.vn.EduQuest.payload.response.ClassValidationResponse;
import com.vn.EduQuest.payload.response.EnrollmentResponse;
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
    public EnrollmentResponse joinClass(String authHeader, JoinClassRequest joinClassRequest) throws CustomException {
        // Validate that class code is provided
        if (joinClassRequest.getClassCode() == null || joinClassRequest.getClassCode().trim().isEmpty()) {
            throw new CustomException(StatusCode.BAD_REQUEST, "Class code is required");
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
            throw new CustomException(StatusCode.BAD_REQUEST, "You must be logged in to join a class");
        }
        
        // Find the student record associated with the user
        Student student = studentRepository.findByUser(currentUser)
                .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_A_STUDENT, 
                        "User is not registered as a student"));
        
        // Find the class by class code
        Class classToJoin = classRepository.findByClassCode(joinClassRequest.getClassCode())
                .orElseThrow(() -> new CustomException(StatusCode.CLASS_NOT_FOUND_BY_CODE));        // Check if the student is already enrolled in the class
        Optional<Enrollment> existingEnrollment = enrollmentRepository.findByStudentAndClazz(student, classToJoin);
        if (existingEnrollment.isPresent()) {
            throw new CustomException(StatusCode.STUDENT_ALREADY_ENROLLED_IN_CLASS);
        }

        // Create new enrollment
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setStudent(student);
        newEnrollment.setClazz(classToJoin);
        newEnrollment.setStatus("ENROLLED");

        Enrollment savedEnrollment = enrollmentRepository.save(newEnrollment);        return new EnrollmentResponse(
                savedEnrollment.getId(),
                student.getId(),
                currentUser.getName(),
                classToJoin.getId(),
                classToJoin.getName(),
                savedEnrollment.getCreatedAt(),
                "Successfully enrolled in class: " + classToJoin.getName()
        );
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
        
        // Require authentication for leaving classes
        if (currentUser == null) {
            throw new CustomException(StatusCode.BAD_REQUEST, "You must be logged in to leave a class");
        }
        
        // Find the student record associated with the user
        Student student = studentRepository.findByUser(currentUser)
                .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_A_STUDENT, 
                        "User is not registered as a student"));
                        
        // Find the class by ID
        Class classToLeave = classRepository.findById(classId)
                .orElseThrow(() -> new CustomException(StatusCode.CLASS_NOT_FOUND_BY_ID, "Class not found with ID: " + classId));        // Find the enrollment
        Enrollment enrollment = enrollmentRepository.findByStudentAndClazz(student, classToLeave)
                .orElseThrow(() -> new CustomException(StatusCode.STUDENT_NOT_ENROLLED_IN_CLASS, "You are not enrolled in this class."));

        // Delete the enrollment
        enrollmentRepository.delete(enrollment);

        return true;
    }

    /**
     * Validates a class code without creating an enrollment
     * Used when user just wants to check if a class code is valid
     */
    @Override
    public ClassValidationResponse validateClassCode(String classCode) throws CustomException {
        // Validate input
        if (classCode == null || classCode.trim().isEmpty()) {
            throw new CustomException(StatusCode.BAD_REQUEST, "Class code is required");
        }
        
        // Find class by code
        Class classToJoin = classRepository.findByClassCode(classCode)
                .orElseThrow(() -> new CustomException(StatusCode.CLASS_NOT_FOUND_BY_CODE, 
                        "Class not found with code: " + classCode));
        
        // Get instructor name
        String instructorName = (classToJoin.getInstructor() != null) ? 
                classToJoin.getInstructor().getName() : "No instructor assigned";
          // Get enrollment count
        int enrollmentCount = (int) enrollmentRepository.countByClazz(classToJoin);
        
        return new ClassValidationResponse(
                classToJoin.getId(),
                classToJoin.getName(),
                classToJoin.getClassCode(),
                instructorName,
                enrollmentCount,
                "Class found. Please login to join this class."
        );
    }
}
