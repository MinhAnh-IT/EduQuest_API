package com.vn.EduQuest.utills;

import com.vn.EduQuest.entities.SubmissionAnswer;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.services.QuestionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GradingServiceImpl implements GradingService{
    QuestionService questionService;

    @Override
    public float autoGrade(List<SubmissionAnswer> submissionAnswerList, int totalQuestions) throws CustomException {
        int correctAnswers = 0;
        for (SubmissionAnswer answer : submissionAnswerList) {
            long questionId = answer.getExerciseQuestion().getQuestion().getId();
            if (answer.getAnswer().getId() == questionService.getCorrectAnswerId(questionId)) {
                correctAnswers++;
            }
        }
        float rawScore = (float) correctAnswers / totalQuestions * 10;
        log.info("Raw score calculated: {}", rawScore);
        return Math.round(rawScore * 2) / 2.0f;

    }
}
