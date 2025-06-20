package com.vn.EduQuest.services;

import org.springframework.stereotype.Service;

import com.vn.EduQuest.entities.Class;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.ClassMapper;
import com.vn.EduQuest.payload.response.clazz.ClassDetailResponse;
import com.vn.EduQuest.repositories.ClassRepository;
import com.vn.EduQuest.repositories.EnrollmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {
    
    private final ClassRepository classRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ClassMapper classMapper;    @Override
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
}
