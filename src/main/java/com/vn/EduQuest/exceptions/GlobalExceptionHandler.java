package com.vn.EduQuest.exceptions;

import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.payload.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<String>> handleCustomException(CustomException ex) {
        log.error("CustomException occurred: {}", ex.getMessage());
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.info("Validation error: {}", ex.getMessage());
        ApiResponse<?> response = ApiResponse.builder()
                .code(StatusCode.VALIDATION_ERROR.getCode())
                .message(StatusCode.VALIDATION_ERROR.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        ApiResponse<?> response = ApiResponse.builder()
                .code(StatusCode.INTERNAL_SERVER_ERROR.getCode())
                .message("An unexpected error occurred")
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}