package com.vn.EduQuest.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.vn.EduQuest.payload.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUserNotFoundException(UserNotFoundException ex) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(404)
                .message(ex.getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception ex) {
        return ResponseEntity.status(500)
                .body(ApiResponse.<String>builder()
                        .code(500)
                        .message("An error occurred: " + ex.getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(500)
                .body(ApiResponse.<String>builder()
                        .code(500)
                        .message("A runtime error occurred: " + ex.getMessage())
                        .data(null)
                        .build());
    }
}