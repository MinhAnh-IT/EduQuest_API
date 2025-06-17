package com.vn.EduQuest.services;

import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.student.JoinClassRequest;

public interface EnrollmentService {

    /**
     * Join a class - requires authenticated user
     */
    boolean joinClass(String authHeader, JoinClassRequest joinClassRequest) throws CustomException;

    /**
     * Validate a class code without joining
     */
    boolean validateClassCode(String classCode) throws CustomException;

    /**
     * Leave a class
     */
    boolean leaveClass(String authHeader, Long classId) throws CustomException;
}
