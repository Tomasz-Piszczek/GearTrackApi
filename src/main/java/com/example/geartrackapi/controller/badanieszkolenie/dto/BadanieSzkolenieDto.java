package com.example.geartrackapi.controller.badanieszkolenie.dto;

import com.example.geartrackapi.dao.model.BadanieSzkolenieStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class BadanieSzkolenieDto {
    private UUID id;
    private UUID employeeId;
    private String employeeFirstName;
    private String employeeLastName;
    private LocalDate date;
    private String category;
    private BadanieSzkolenieStatus status;
}
