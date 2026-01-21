package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.machine.dto.MachineDto;
import com.example.geartrackapi.dao.model.Machine;
import com.example.geartrackapi.dao.model.MachineInspection;
import com.example.geartrackapi.dao.repository.MachineInspectionRepository;
import com.example.geartrackapi.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MachineMapper {
    
    private final MachineInspectionRepository machineInspectionRepository;
    
    public MachineDto toDto(Machine machine) {
        String employeeName = null;
        if (machine.getEmployee() != null) {
            employeeName = machine.getEmployee().getFirstName() + " " + machine.getEmployee().getLastName();
        }
        
        LocalDate nextInspectionDate = null;
        LocalDate lastInspectionDate = null;
        long totalInspections = 0;
        
        try {
            Optional<MachineInspection> nextInspection = machineInspectionRepository
                    .findNextInspectionByMachineId(machine.getOrganizationId(), machine.getId(), LocalDate.now());
            if (nextInspection.isPresent()) {
                nextInspectionDate = nextInspection.get().getInspectionDate();
            }
            
            List<MachineInspection> lastInspections = machineInspectionRepository
                    .findLastInspectionByMachineId(machine.getOrganizationId(), machine.getId(), LocalDate.now());
            if (!lastInspections.isEmpty()) {
                lastInspectionDate = lastInspections.get(0).getInspectionDate();
            }
            
            totalInspections = machineInspectionRepository.countByOrganizationIdAndMachineId(machine.getOrganizationId(), machine.getId());
        } catch (Exception e) {
        }
        
        return MachineDto.builder()
                .uuid(machine.getId())
                .name(machine.getName())
                .factoryNumber(machine.getFactoryNumber())
                .employeeId(machine.getEmployeeId())
                .employeeName(employeeName)
                .nextInspectionDate(nextInspectionDate)
                .lastInspectionDate(lastInspectionDate)
                .totalInspections(totalInspections)
                .build();
    }
    
    public Machine toEntity(MachineDto dto) {
        return Machine.builder()
                .name(dto.getName())
                .factoryNumber(dto.getFactoryNumber())
                .employeeId(dto.getEmployeeId())
                .organizationId(SecurityUtils.getCurrentOrganizationId())
                .build();
    }
    
    public Machine updateEntity(Machine existing, MachineDto dto) {
        return Machine.builder()
                .id(existing.getId())
                .name(dto.getName())
                .factoryNumber(dto.getFactoryNumber())
                .employeeId(dto.getEmployeeId())
                .organizationId(existing.getOrganizationId())
                .build();
    }
}