package com.example.geartrackapi.exception;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
public class WorkingHoursConflictException extends RuntimeException {
    private final Map<String, List<LocalDate>> conflicts;

    public WorkingHoursConflictException(String message, Map<String, List<LocalDate>> conflicts) {
        super(message);
        this.conflicts = conflicts;
    }
}
