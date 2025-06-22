package com.vn.EduQuest.services;

import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.UpdateRequest;
import com.vn.EduQuest.payload.response.UpdateResponse;

public interface UpdateService {
    UpdateResponse updateProfiles(Long userId, UpdateRequest request) throws CustomException;
    UpdateResponse getProfile(Long userId) throws CustomException;
}
