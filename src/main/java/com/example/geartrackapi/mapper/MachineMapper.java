package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.machine.dto.MachineDto;
import com.example.geartrackapi.dao.model.Machine;
import com.example.geartrackapi.dao.model.MachineInspection;
import com.example.geartrackapi.dao.repository.MachineInspectionRepository;
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
            // Get next inspection (future inspections)
            Optional<MachineInspection> nextInspection = machineInspectionRepository
                    .findNextInspectionByMachineId(machine.getUserId(), machine.getId(), LocalDate.now());
            if (nextInspection.isPresent()) {
                nextInspectionDate = nextInspection.get().getInspectionDate();
            }
            
            // Get last inspection (past inspections)
            List<MachineInspection> lastInspections = machineInspectionRepository
                    .findLastInspectionByMachineId(machine.getUserId(), machine.getId(), LocalDate.now());
            if (!lastInspections.isEmpty()) {
                lastInspectionDate = lastInspections.get(0).getInspectionDate();
            }
            
            // Get total inspections count
            totalInspections = machineInspectionRepository.countByUserIdAndMachineId(machine.getUserId(), machine.getId());
        } catch (Exception e) {
            // If inspection queries fail, continue with null values
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
        Machine machine = new Machine();
        machine.setName(dto.getName());
        machine.setFactoryNumber(dto.getFactoryNumber());
        machine.setEmployeeId(dto.getEmployeeId());
        return machine;
    }
    
    public void updateEntity(Machine machine, MachineDto dto) {
        machine.setName(dto.getName());
        machine.setFactoryNumber(dto.getFactoryNumber());
        machine.setEmployeeId(dto.getEmployeeId());
    }
}