package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.SubmissionAnswer;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.repositories.SubmissionAnswerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubmissionAnswerServiceImpl implements SubmissionAnswerService{
    SubmissionAnswerRepository submissionAnswerRepository;

    @Override
    public void saveAllSubmissionAnswer(List<SubmissionAnswer> submissionAnswer) throws CustomException {
        submissionAnswerRepository.saveAll(submissionAnswer);
    }
}
