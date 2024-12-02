package com.sparta.msa_exam.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<Map<String, Object>> validationErrors = new ArrayList<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            Map<String, Object> fieldError = new HashMap<>();
            fieldError.put("field", error.getField());
            fieldError.put("rejectedValue", error.getRejectedValue());
            fieldError.put("message", error.getDefaultMessage());
            validationErrors.add(fieldError);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.put("path", "/orders");
        response.put("validationErrors", validationErrors);
        response.put("additionalInfo", "요청 데이터를 확인하고 다시 시도하세요.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
