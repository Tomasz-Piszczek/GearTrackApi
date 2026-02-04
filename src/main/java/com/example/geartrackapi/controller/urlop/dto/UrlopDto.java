package com.example.geartrackapi.controller.urlop.dto;

import com.example.geartrackapi.dao.model.UrlopCategory;
import com.example.geartrackapi.dao.model.UrlopStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class UrlopDto {
    private UUID id;
    private UUID employeeId;
    private String employeeFirstName;
    private String employeeLastName;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String note;
    private UrlopStatus status;
    private UrlopCategory category;
    private BigDecimal categoryRate;
}
