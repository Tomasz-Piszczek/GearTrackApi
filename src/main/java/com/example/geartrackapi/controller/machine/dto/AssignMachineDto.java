package com.example.geartrackapi.controller.machine.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AssignMachineDto {
    private UUID machineId;
    private UUID employeeId;
}