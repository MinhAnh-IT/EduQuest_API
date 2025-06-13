package com.vn.EduQuest.exceptions;

import org.springframework.http.ResponseEntity;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.payload.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<String>> handleCustomException(CustomException ex) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.ok(response);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.info("Validation error: {}", ex.getMessage());
        ApiResponse<?> exception = ApiResponse.<Object>builder()
                .code(StatusCode.VALIDATION_ERROR.getCode())
                .message(StatusCode.VALIDATION_ERROR.getMessage())
                .build();
        return ResponseEntity.ok(exception);
        }
    }
