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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {    private final ClassRepository classRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;@Override
    @Transactional
    public EnrollmentResponse joinClass(User currentUser, JoinClassRequest joinClassRequest) throws CustomException {
        // Validate that currentUser is not null
        if (currentUser == null) {
            throw new CustomException(StatusCode.BAD_REQUEST, "User authentication is required");
        }
        
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
        newEnrollment.setStudent(student); // Using Student entity now, not User
        newEnrollment.setClazz(classToJoin);
        newEnrollment.setStatus("ENROLLED");

        Enrollment savedEnrollment = enrollmentRepository.save(newEnrollment);

        return new EnrollmentResponse(
                savedEnrollment.getId(),
                currentUser.getId(),
                currentUser.getFullName(),
                classToJoin.getId(),
                classToJoin.getName(),
                savedEnrollment.getCreatedAt(),
                "Successfully enrolled in class: " + classToJoin.getName()
        );    }
    
    // The joinClassMobile method has been removed as we now require all users to be authenticated
    
    @Override
    @Transactional
    public boolean leaveClass(User currentUser, Long classId) throws CustomException {
        // Find the student record associated with the user
        Student student = studentRepository.findByUser(currentUser)
                .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_A_STUDENT, 
                        "User is not registered as a student"));
                        
        // Find the class by ID
        Class classToLeave = classRepository.findById(classId)
                .orElseThrow(() -> new CustomException(StatusCode.CLASS_NOT_FOUND_BY_ID, "Class not found with ID: " + classId));

        // Find the enrollment
        Enrollment enrollment = enrollmentRepository.findByStudentAndClazz(student, classToLeave)
                .orElseThrow(() -> new CustomException(StatusCode.STUDENT_NOT_ENROLLED_IN_CLASS, "You are not enrolled in this class."));

        // Delete the enrollment
        enrollmentRepository.delete(enrollment);

        return true; // Return true on success
    }    /**
     * Validates a class code without creating an enrollment
     * Used when user just wants to check if a class code is valid
     */
    @Override
    public ClassValidationResponse validateClassCode(String classCode) throws CustomException {
        if (classCode == null || classCode.trim().isEmpty()) {
            throw new CustomException(StatusCode.BAD_REQUEST, "Class code is required");
        }
        
        // Find the class by class code
        Class classToJoin = classRepository.findByClassCode(classCode)
                .orElseThrow(() -> new CustomException(StatusCode.CLASS_NOT_FOUND_BY_CODE, 
                        "Class not found with code: " + classCode));
        
        // Get instructor information (if available)
        User instructor = classToJoin.getInstructor();
        String instructorName = instructor != null ? instructor.getFullName() : "Not assigned";
        
        // Get enrollment count
        int enrollmentCount = classToJoin.getEnrollments() != null ? classToJoin.getEnrollments().size() : 0;
        
        // Return class information without null student fields using builder pattern
        return ClassValidationResponse.builder()
                .classId(classToJoin.getId())
                .className(classToJoin.getName())
                .classCode(classCode)
                .instructorName(instructorName)
                .enrollmentCount(enrollmentCount)
                .message("Class found. Please login to join this class.")
                .build();
    }
}
