package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.machine.dto.MachineInspectionDto;
import com.example.geartrackapi.dao.model.Machine;
import com.example.geartrackapi.dao.model.MachineInspection;
import com.example.geartrackapi.dao.repository.MachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class MachineInspectionMapper {
    
    private final MachineRepository machineRepository;
    
    public MachineInspectionDto toDto(MachineInspection inspection) {
        Machine machine = machineRepository.findById(inspection.getMachineId()).orElse(null);
        
        return MachineInspectionDto.builder()
                .uuid(inspection.getId())
                .machineId(inspection.getMachineId())
                .machineName(machine != null ? machine.getName() : "Unknown")
                .machineFactoryNumber(machine != null ? machine.getFactoryNumber() : "Unknown")
                .inspectionDate(inspection.getInspectionDate())
                .performedBy(inspection.getPerformedBy())
                .notes(inspection.getNotes())
                .status(inspection.getStatus())
                .createdAt(inspection.getCreatedAt() != null ? inspection.getCreatedAt().toLocalDate() : null)
                .updatedAt(inspection.getUpdatedAt() != null ? inspection.getUpdatedAt().toLocalDate() : null)
                .build();
    }
}