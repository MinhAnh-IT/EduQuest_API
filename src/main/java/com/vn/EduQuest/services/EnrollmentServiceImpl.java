package com.vn.EduQuest.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vn.EduQuest.payload.request.Class.EnrollmentApprovalRequest;
import com.vn.EduQuest.payload.response.clazz.EnrollmentResponsee;
import com.vn.EduQuest.payload.response.enrollment.PendingEnrollmentResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.EduQuest.entities.Class;
import com.vn.EduQuest.entities.Enrollment;
import com.vn.EduQuest.entities.Student;
import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.EnrollmentStatus;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.EnrollmentMapper;
import com.vn.EduQuest.payload.request.student.JoinClassRequest;
import com.vn.EduQuest.payload.response.enrollment.EnrollmentResponse;
import com.vn.EduQuest.repositories.ClassRepository;
import com.vn.EduQuest.repositories.EnrollmentRepository;
import com.vn.EduQuest.repositories.StudentRepository;
import com.vn.EduQuest.repositories.UserRepository;
import com.vn.EduQuest.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class EnrollmentServiceImpl implements EnrollmentService {    
    private final ClassRepository classRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final EnrollmentMapper enrollmentMapper;    private User getCurrentUser() throws CustomException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new CustomException(StatusCode.AUTHENTICATION_REQUIRED);
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findById(userDetails.getId())

            .orElseThrow(() -> new CustomException(StatusCode.AUTHENTICATION_REQUIRED));
    }
    
    @Override
    @Transactional
    public boolean joinClass(String authHeader, JoinClassRequest joinClassRequest) throws CustomException {          // Validate that class code is provided
        if (joinClassRequest.getClassCode() == null || joinClassRequest.getClassCode().trim().isEmpty()) {
            throw new CustomException(StatusCode.CLASS_CODE_REQUIRED);
        }

        // Get current authenticated user
        User currentUser = getCurrentUser();

        try {            // Find the student record associated with the user
            Student student = studentRepository.findByUser(currentUser)

                    .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_A_STUDENT));

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
            throw e;        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public boolean leaveClass(String authHeader, Long classId) throws CustomException {
        // Get current authenticated user
        User currentUser = getCurrentUser();        try {
            Student student = studentRepository.findByUser(currentUser)

                    .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_A_STUDENT));
            Class classToLeave = classRepository.findById(classId)
                    .orElseThrow(() -> new CustomException(StatusCode.CLASS_NOT_FOUND_BY_ID));
            Enrollment enrollment = enrollmentRepository.findByStudentAndClazz(student, classToLeave)
                    .orElseThrow(() -> new CustomException(StatusCode.STUDENT_NOT_ENROLLED_IN_CLASS));
            enrollmentRepository.delete(enrollment);
            return true;
        } catch (CustomException e) {
            throw e;        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR);
        }

    }    @Override
    public boolean validateClassCode(String classCode) throws CustomException {     
        if (classCode == null || classCode.trim().isEmpty()) {
            throw new CustomException(StatusCode.CLASS_CODE_REQUIRED);
        }

        try {
            classRepository.findByClassCode(classCode)

                    .orElseThrow(() -> new CustomException(StatusCode.CLASS_NOT_FOUND_BY_CODE));

            return true; // Return true if class code is valid
        } catch (CustomException e) {
            throw e;        
        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    public List<EnrollmentResponse> getStudentEnrollments(String authHeader) throws CustomException {
        // Get current authenticated user
        User currentUser = getCurrentUser();

        try {            // Find the student record associated with the user
            Student student = studentRepository.findByUser(currentUser)
                    .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_A_STUDENT));

            // Get all enrollments for this student
            List<Enrollment> enrollments = enrollmentRepository.findByStudentOrderByCreatedAtDesc(student);

            // Convert to response DTOs
            return enrollments.stream()
                    .map(enrollmentMapper::toEnrollmentResponse)
                    .collect(Collectors.toList());

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<EnrollmentResponse> getStudentEnrolledClasses(String authHeader) throws CustomException {
        // Get current authenticated user
        User currentUser = getCurrentUser();

        try {            // Find the student record associated with the user
            Student student = studentRepository.findByUser(currentUser)
                    .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_A_STUDENT));

            // Get only ENROLLED status enrollments for this student
            List<Enrollment> enrollments = enrollmentRepository.findByStudentAndStatusOrderByCreatedAtDesc(student, EnrollmentStatus.ENROLLED);

            // Convert to response DTOs
            return enrollments.stream()
                    .map(enrollmentMapper::toEnrollmentResponse)
                    .collect(Collectors.toList());

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<PendingEnrollmentResponse> getPendingEnrollments(Long instructorID, Long classId) throws CustomException {
        try {
            Class clazz = classRepository.findById(classId)
                    .orElseThrow(() -> new CustomException(StatusCode.CLASS_NOT_FOUND_BY_ID,classId));

            List<Enrollment> pendingEnrollments = enrollmentRepository.findByClazzAndStatus(clazz, EnrollmentStatus.PENDING);
            if (pendingEnrollments.isEmpty()) {
                throw new CustomException(StatusCode.PENDING_ENROLLMENT_EMPTY, classId);
            }
            return pendingEnrollments.stream()
                    .map(enrollmentMapper::toPendingResponse)
                    .collect(Collectors.toList());
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR,
                    e.getMessage());
        }
    }
    @Override
    public EnrollmentResponsee enrollStudent(Long instructorID, EnrollmentApprovalRequest request) throws CustomException {

        try{
            Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId())
                    .orElseThrow(() -> new CustomException(StatusCode.ENROLLMENT_NOT_FOUND,request.getEnrollmentId()));
            Class clazz = enrollment.getClazz();
            User instructor = userRepository.findById(instructorID)
                    .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
            if(!clazz.getInstructor().getId().equals(instructor.getId())) {
                throw new CustomException(StatusCode.FORBIDDEN);
            }
            if (request.getStatus() != EnrollmentStatus.ENROLLED && request.getStatus() != EnrollmentStatus.REJECTED) {
                throw new CustomException(StatusCode.BAD_REQUEST, "Invalid status");
            }
            enrollment.setStatus(request.getStatus());
            Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
            return enrollmentMapper.toApproveResponse(updatedEnrollment);
        }
        catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }
}
