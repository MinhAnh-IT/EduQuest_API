package com.vn.EduQuest.services;

import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.response.clazz.ClassDetailResponse;

public interface ClassService {
    
    /**
     * Get class detail by class ID
     */
    ClassDetailResponse getClassDetail(Long classId) throws CustomException;
}
