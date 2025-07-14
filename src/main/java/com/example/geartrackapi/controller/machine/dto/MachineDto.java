package com.example.geartrackapi.controller.machine.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MachineDto {
    private UUID uuid;
    private String name;
    private String factoryNumber;
    private UUID employeeId;
    private String employeeName;
}