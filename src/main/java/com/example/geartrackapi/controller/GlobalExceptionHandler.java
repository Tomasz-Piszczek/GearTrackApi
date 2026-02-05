package com.example.geartrackapi.controller;

import com.example.geartrackapi.exception.WorkingHoursConflictException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WorkingHoursConflictException.class)
    public ResponseEntity<Map<String, Object>> handleWorkingHoursConflictException(WorkingHoursConflictException ex) {
        log.warn("[handleWorkingHoursConflictException] {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());

        List<Map<String, Object>> conflicts = ex.getConflicts().entrySet().stream()
                .map(entry -> {
                    Map<String, Object> conflict = new HashMap<>();
                    conflict.put("employeeName", entry.getKey());
                    conflict.put("conflictDates", entry.getValue());
                    return conflict;
                })
                .collect(Collectors.toList());

        response.put("conflicts", conflicts);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
