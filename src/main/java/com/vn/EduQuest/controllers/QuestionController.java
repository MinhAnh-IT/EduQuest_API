package com.vn.EduQuest.controllers;

import com.vn.EduQuest.entities.Question;

import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.QuestionMapper;
import com.vn.EduQuest.payload.ApiResponse;
import com.vn.EduQuest.payload.request.question.QuestionCreateRequest;
import com.vn.EduQuest.payload.response.question.QuestionCreateResponse;
import com.vn.EduQuest.security.UserDetailsImpl;
import com.vn.EduQuest.services.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    QuestionService questionService;
    QuestionMapper questionMapper;

    @PostMapping("/create")
    public ResponseEntity<?> createQuestion(@Valid @RequestBody QuestionCreateRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) throws CustomException {
        Question questionEntity = questionMapper.toEntity(request);
        QuestionCreateResponse questionCreate = questionService.createQuestion(questionEntity, userDetails.getId());
        ApiResponse<?> response = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.CREATED.getMessage())
                .data(questionCreate)
                .build();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update/{questionId}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long questionId, @Valid @RequestBody QuestionCreateRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) throws CustomException {
        Question questionEntity = questionMapper.toEntity(request);
        QuestionCreateResponse questionUpdate = questionService.updateQuestion(questionId, questionEntity, userDetails.getId());
        ApiResponse<?> response = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(questionUpdate)
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/created-by-me")
    public ResponseEntity<?> getQuestionsCreatedByMe(@AuthenticationPrincipal UserDetailsImpl userDetails) throws CustomException {
        List<QuestionCreateResponse> questions = questionService.getQuestionsCreatedByInstructor(userDetails.getId());
        ApiResponse<?> response = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(questions)
                .build();
        return ResponseEntity.ok(response);
    }
}
