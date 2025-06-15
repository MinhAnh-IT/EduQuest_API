package com.vn.EduQuest.services;

import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.JoinClassRequest;
import com.vn.EduQuest.payload.response.ClassValidationResponse;
import com.vn.EduQuest.payload.response.EnrollmentResponse;

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
