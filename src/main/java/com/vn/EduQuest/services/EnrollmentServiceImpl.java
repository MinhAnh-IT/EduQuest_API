package com.vn.EduQuest.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.EduQuest.entities.Class;
import com.vn.EduQuest.entities.Enrollment;
import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.Role;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.JoinClassRequest;
import com.vn.EduQuest.payload.response.EnrollmentResponse;
import com.vn.EduQuest.repositories.ClassRepository;
import com.vn.EduQuest.repositories.EnrollmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final ClassRepository classRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional
    public EnrollmentResponse joinClass(User currentUser, JoinClassRequest joinClassRequest) throws CustomException {
        // Ensure the user is a student
        if (!currentUser.getRole().equals(Role.STUDENT)) {
            throw new CustomException(StatusCode.USER_NOT_A_STUDENT, "Only students can join classes.");
        }

        // Find the class by class code
        Class classToJoin = classRepository.findByClassCode(joinClassRequest.getClassCode())
                .orElseThrow(() -> new CustomException(StatusCode.CLASS_NOT_FOUND_BY_CODE));

        // Check if the student is already enrolled in the class
        Optional<Enrollment> existingEnrollment = enrollmentRepository.findByStudentAndClazz(currentUser, classToJoin);
        if (existingEnrollment.isPresent()) {
            throw new CustomException(StatusCode.STUDENT_ALREADY_ENROLLED_IN_CLASS);
        }

        // Create new enrollment
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setStudent(currentUser);
        newEnrollment.setClazz(classToJoin);
        newEnrollment.setEnrollmentDate(LocalDateTime.now());

        Enrollment savedEnrollment = enrollmentRepository.save(newEnrollment);

        return new EnrollmentResponse(
                savedEnrollment.getId(),
                currentUser.getId(),
                currentUser.getFullName(), // Changed to getFullName()
                classToJoin.getId(),
                classToJoin.getName(),
                savedEnrollment.getEnrollmentDate(),
                "Successfully enrolled in class: " + classToJoin.getName()
        );
    }
}
