package com.vn.EduQuest.payload.projection;

import com.vn.EduQuest.entities.Question;

public interface QuestionWithExerciseQuestionId {
    Question getQuestion();
    Long getEqId();
}
