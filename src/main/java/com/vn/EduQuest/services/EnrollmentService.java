package com.vn.EduQuest.services;

import java.util.List;

import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.Class.EnrollmentApprovalRequest;
import com.vn.EduQuest.payload.request.student.JoinClassRequest;
import com.vn.EduQuest.payload.response.clazz.EnrollmentResponsee;
import com.vn.EduQuest.payload.response.enrollment.EnrollmentResponse;
import com.vn.EduQuest.payload.response.enrollment.PendingEnrollmentResponse;

public interface EnrollmentService {

    boolean joinClass(String authHeader, JoinClassRequest joinClassRequest) throws CustomException;

    boolean validateClassCode(String classCode) throws CustomException;    
     
    boolean leaveClass(String authHeader, Long classId) throws CustomException;
  
    List<EnrollmentResponse> getStudentEnrollments(String authHeader) throws CustomException;
    
    List<EnrollmentResponse> getStudentEnrolledClasses(String authHeader) throws CustomException;
    EnrollmentResponsee enrollStudent(Long instructorID, EnrollmentApprovalRequest request) throws CustomException;
    List<PendingEnrollmentResponse> getPendingEnrollments(Long instructorID ,Long classId) throws CustomException;

}