package com.vn.EduQuest.utills;


import com.vn.EduQuest.entities.SubmissionAnswer;
import com.vn.EduQuest.exceptions.CustomException;

import java.util.List;

public interface GradingService {
    float autoGrade(List<SubmissionAnswer> submissionAnswerList, int totalQuestions) throws CustomException;
}
