package com.vn.EduQuest.controllers;

import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.payload.ApiResponse;
import com.vn.EduQuest.payload.response.ResultDTO;
import com.vn.EduQuest.security.UserDetailsImpl;
import com.vn.EduQuest.services.ResultService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResultController {

    ResultService resultService;


    @GetMapping("/{exerciseId}")
    public ResponseEntity<ApiResponse<ResultDTO>> getResult(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long exerciseId) throws Exception {
        ResultDTO result = resultService.getResult(userDetails.getId(), exerciseId);
        ApiResponse<ResultDTO> response = ApiResponse.<ResultDTO>builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())  
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }
}