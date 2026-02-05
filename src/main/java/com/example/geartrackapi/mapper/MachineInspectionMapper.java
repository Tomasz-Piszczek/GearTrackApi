package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.machine.dto.MachineInspectionDto;
import com.example.geartrackapi.controller.machine.dto.CreateMachineInspectionDto;
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
                .performedBy(inspection.getPerformedBy())
                .createdAt(inspection.getCreatedAt().toLocalDate())
                .updatedAt(inspection.getUpdatedAt().toLocalDate())
                .build();
    }
    
    public MachineInspection updateEntity(MachineInspection existing, CreateMachineInspectionDto dto) {
        return MachineInspection.builder()
                .id(existing.getId())
                .machineId(existing.getMachineId())
                .inspectionDate(dto.getInspectionDate())
                .notes(dto.getNotes())
                .status(dto.getStatus() != null ? dto.getStatus() : existing.getStatus())
                .performedBy(dto.getPerformedBy() != null ? dto.getPerformedBy() : existing.getPerformedBy())
                .organizationId(existing.getOrganizationId())
                .createdAt(existing.getCreatedAt())
                .build();
    }
}