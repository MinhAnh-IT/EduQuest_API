package com.vn.EduQuest.exceptions;

import com.vn.EduQuest.payload.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> ExceptionHandler(CustomException ex){
        ApiResponse<?> exception = ApiResponse.<Object>builder()
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.ok(exception);
    }


}