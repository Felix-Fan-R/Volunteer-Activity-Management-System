package com.eco.volunteer.exception;

import org.hibernate.JDBCException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.eco.volunteer.dto.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        String message = "操作失败";
        if (e instanceof RuntimeException) {
            message = e.getMessage();
        } else if (e instanceof DataIntegrityViolationException) {
            message = "数据完整性错误，请检查输入";
        } else if (e instanceof JDBCException) {
            message = "数据库操作失败，请稍后重试";
        }
        return ResponseEntity.badRequest().body(new ApiResponse(message, null));
    }
}

class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 