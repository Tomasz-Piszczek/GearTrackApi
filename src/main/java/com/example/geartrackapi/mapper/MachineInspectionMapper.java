package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.machine.dto.MachineInspectionDto;
import com.example.geartrackapi.dao.model.Machine;
import com.example.geartrackapi.dao.model.MachineInspection;
import com.example.geartrackapi.dao.repository.MachineRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class MachineInspectionMapper {
    
    private final MachineRepository machineRepository;
    
    public MachineInspectionDto toDto(MachineInspection inspection) {
        Machine machine = machineRepository.findById(inspection.getMachineId()).orElseThrow(
                () -> new EntityNotFoundException("Machine with id:" +inspection.getMachineId() + "not found"));
        
        return MachineInspectionDto.builder()
                .uuid(inspection.getId())
                .machineId(inspection.getMachineId())
                .machineName(machine.getName())
                .machineFactoryNumber(machine.getFactoryNumber())
                .inspectionDate(inspection.getInspectionDate())
                .notes(inspection.getNotes())
                .status(inspection.getStatus())
                .createdAt(inspection.getCreatedAt().toLocalDate())
                .updatedAt(inspection.getUpdatedAt().toLocalDate())
                .build();
    }
}