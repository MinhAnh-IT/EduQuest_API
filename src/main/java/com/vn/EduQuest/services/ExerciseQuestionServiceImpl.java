package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.ExerciseQuestion;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.repositories.ExerciseQuestionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseQuestionServiceImpl implements ExerciseQuestionService{
    ExerciseQuestionRepository exerciseQuestionRepository;

    @Override
    public ExerciseQuestion getExerciseQuestionById(long exerciseId) throws CustomException {
        return exerciseQuestionRepository.findById(exerciseId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "Exercise", exerciseId));
    }

    @Override
    public void saveExerciseQuestion(ExerciseQuestion exerciseQuestion) throws CustomException {
        exerciseQuestionRepository.save(exerciseQuestion);
    }
}
