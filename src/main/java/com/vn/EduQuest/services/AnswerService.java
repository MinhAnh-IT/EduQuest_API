package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Answer;
import com.vn.EduQuest.exceptions.CustomException;

import java.util.List;

public interface AnswerService {
    Answer getAnswerById(long answerId) throws CustomException;
}
