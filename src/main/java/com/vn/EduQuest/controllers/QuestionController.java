package com.vn.EduQuest.controllers;

import com.vn.EduQuest.entities.Question;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.response.question.QuestionCreateResponse;
import com.vn.EduQuest.security.UserDetailsImpl;
import com.vn.EduQuest.services.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mapstruct.Mapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/api/questions")
@RestController
public class QuestionController {

    QuestionService questionService;
    @Mapping("/create")
    public QuestionCreateResponse createQuestion(@AuthenticationPrincipal UserDetailsImpl userDetails, QuestionCreateResponse questionCreateResponse) throws CustomException {
        Question question = questionService.C;
        return questionCreateResponse;
}
