package com.vn.EduQuest.services;

import java.util.List;
import java.util.stream.Collectors;

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
public class ClassServiceImpl implements ClassService {
    
    private final ClassRepository classRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ClassMapper classMapper;
    private final StudentMapper studentMapper;    @Override
    public ClassDetailResponse getClassDetail(Long classId) throws CustomException {
        try {
            // Find class by ID
            Class clazz = classRepository.findById(classId)
                    .orElseThrow(() -> new CustomException(StatusCode.CLASS_NOT_FOUND_BY_ID, 
                            "Class not found with ID: " + classId));

            // Get student count for this class
            long studentCount = enrollmentRepository.countByClazz(clazz);

            // Map to response DTO
            ClassDetailResponse response = classMapper.toClassDetailResponse(clazz);
            response.setStudentCount((int) studentCount);

            return response;

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR,
                    "Failed to retrieve class detail: " + e.getMessage());
        }
    }

    @Override
    public List<StudentInClassResponse> getStudentsInClass(Long classId) throws CustomException {
        try {
            // Find class by ID
            Class clazz = classRepository.findById(classId)
                    .orElseThrow(() -> new CustomException(StatusCode.CLASS_NOT_FOUND_BY_ID, 
                            "Class not found with ID: " + classId));

            // Get all enrollments for this class
            List<Enrollment> enrollments = enrollmentRepository.findByClazz(clazz);

            // Convert to response DTOs
            return enrollments.stream()
                    .map(studentMapper::toStudentInClassResponse)
                    .collect(Collectors.toList());

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR,
                    "Failed to retrieve students in class: " + e.getMessage());
        }
    }
}
