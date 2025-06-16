package com.vn.EduQuest.services;

import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.response.ResultDTO;

public interface ResultService {
    ResultDTO getResult(Long studentId, Long exerciseId) throws CustomException;
}