package com.vn.EduQuest.services;

import java.util.List;

import com.vn.EduQuest.entities.Class;
import com.vn.EduQuest.entities.Student;
import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.Class.ClassCreateRequest;
import com.vn.EduQuest.payload.response.clazz.ClassCreateResponse;
import com.vn.EduQuest.payload.response.clazz.ClassDetailResponse;
import com.vn.EduQuest.payload.response.clazz.ClassSimpleForTeacher;
import com.vn.EduQuest.payload.response.clazz.InstructorClassResponse;
import com.vn.EduQuest.payload.response.student.StudentInClassResponse;

public interface ClassService {
    
    ClassDetailResponse getClassDetail(Long classId) throws CustomException;

    List<StudentInClassResponse> getStudentsInClass(Long instructorId, Long classId) throws CustomException;

    List<StudentInClassResponse> getEnrolledStudentsInClass(Long classId) throws CustomException;
    
    ClassCreateResponse createClass(Long InstructorID, ClassCreateRequest request) throws CustomException;
    List<InstructorClassResponse> getInstructorClasses(Long instructorId) throws CustomException;
    Class getClassById(Long classId) throws CustomException;
    List<ClassSimpleForTeacher> getClassesForTeacher(Long teacherId) throws CustomException;
    List<Student> getListOfStudentsInClass(Long classId) throws CustomException;
}
