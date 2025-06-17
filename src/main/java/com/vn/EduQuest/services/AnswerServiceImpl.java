package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Answer;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.repositories.AnswerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerServiceImpl implements AnswerService{
    AnswerRepository answerRepository;

    @Override
    public Answer getAnswerById(long answerId) throws CustomException {
        return answerRepository.findById(answerId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "answer", answerId));
    }
}
