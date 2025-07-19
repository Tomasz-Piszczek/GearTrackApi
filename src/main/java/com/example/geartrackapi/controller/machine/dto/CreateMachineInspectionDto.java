package com.example.geartrackapi.controller.machine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMachineInspectionDto {
    private UUID machineId;
    
    private LocalDate inspectionDate;
    
    private String performedBy;
    
    private String notes;
    
    private String status;
}