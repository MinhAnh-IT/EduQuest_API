package com.vn.EduQuest.services;

import java.util.List;

import com.vn.EduQuest.entities.Class;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.Class.ClassCreateRequest;
import com.vn.EduQuest.payload.response.clazz.ClassCreateResponse;
import com.vn.EduQuest.payload.response.clazz.ClassDetailResponse;
import com.vn.EduQuest.payload.response.clazz.ClassSimpleForTeacher;
import com.vn.EduQuest.payload.response.clazz.InstructorClassResponse;
import com.vn.EduQuest.payload.response.student.StudentInClassResponse;

public interface ClassService {
    
    /**
     * Get class detail by class ID
     */
    ClassDetailResponse getClassDetail(Long classId) throws CustomException;
    
    /**
     * Get list of students in a class (only for instructor who owns the class)
     */
    List<StudentInClassResponse> getStudentsInClass(Long instructorId, Long classId) throws CustomException;
    
    /**
     * Get list of enrolled students in a class (only ENROLLED status)
     */
    List<StudentInClassResponse> getEnrolledStudentsInClass(Long classId) throws CustomException;
    
    ClassCreateResponse createClass(Long InstructorID, ClassCreateRequest request) throws CustomException;
    List<InstructorClassResponse> getInstructorClasses(Long instructorId) throws CustomException;
    Class getClassById(Long classId) throws CustomException;
    List<ClassSimpleForTeacher> getClassesForTeacher(Long teacherId) throws CustomException;
}
