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
public class MachineInspectionDto {
    private UUID uuid;
    private UUID machineId;
    private String machineName;
    private String machineFactoryNumber;
    private LocalDate inspectionDate;
    private String notes;
    private String status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}