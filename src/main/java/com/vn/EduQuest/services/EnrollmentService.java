package com.vn.EduQuest.services;

import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.student.JoinClassRequest;
import com.vn.EduQuest.payload.response.clazz.ClassValidationResponse;
import com.vn.EduQuest.payload.response.enrollment.EnrollmentResponse;

public interface EnrollmentService {
    /**
     * Join a class - requires authenticated user
     */
    EnrollmentResponse joinClass(String authHeader, JoinClassRequest joinClassRequest) throws CustomException;
    
    /**
     * Validate a class code without joining
     */
    ClassValidationResponse validateClassCode(String classCode) throws CustomException;
    
    /**
     * Leave a class
     */
    boolean leaveClass(String authHeader, Long classId) throws CustomException;
}
