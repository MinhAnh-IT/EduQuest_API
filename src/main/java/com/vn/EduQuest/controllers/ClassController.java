package com.vn.EduQuest.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.ApiResponse;
import com.vn.EduQuest.payload.response.clazz.ClassDetailResponse;
import com.vn.EduQuest.services.ClassService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    @GetMapping("/{classId}/detail")
    public ResponseEntity<?> getClassDetail(@PathVariable Long classId) throws CustomException {
        ClassDetailResponse result = classService.getClassDetail(classId);
        ApiResponse<?> response = ApiResponse.<ClassDetailResponse>builder()
                .code(StatusCode.OK.getCode())
                .message("Successfully retrieved class detail")
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }
}
