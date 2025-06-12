package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.JoinClassRequest;
import com.vn.EduQuest.payload.response.EnrollmentResponse;

public interface EnrollmentService {
    EnrollmentResponse joinClass(User user, JoinClassRequest joinClassRequest) throws CustomException;
}
