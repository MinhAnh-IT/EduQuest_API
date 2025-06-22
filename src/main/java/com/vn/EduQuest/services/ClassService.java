package com.vn.EduQuest.services;

import java.util.List;

import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.response.clazz.ClassDetailResponse;
import com.vn.EduQuest.payload.response.student.StudentInClassResponse;

public interface ClassService {
    
    /**
     * Get class detail by class ID
     */
    ClassDetailResponse getClassDetail(Long classId) throws CustomException;
    
    /**
     * Get list of students in a class
     */
    List<StudentInClassResponse> getStudentsInClass(Long classId) throws CustomException;
}
