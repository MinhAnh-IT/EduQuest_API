package com.vn.EduQuest.controllers;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.payload.request.JoinClassRequest;
import com.vn.EduQuest.payload.ApiResponse;
import com.vn.EduQuest.payload.response.EnrollmentResponse;
import com.vn.EduQuest.services.EnrollmentService;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.enums.StatusCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> joinClass(
            @AuthenticationPrincipal User currentUser, // Get the authenticated user
            @Valid @RequestBody JoinClassRequest joinClassRequest) throws CustomException {
        EnrollmentResponse enrollmentResponse = enrollmentService.joinClass(currentUser, joinClassRequest);
        return new ResponseEntity<>(new ApiResponse<EnrollmentResponse>(StatusCode.OK.getCode(), "Successfully joined class", enrollmentResponse), HttpStatus.OK);
    }
}
