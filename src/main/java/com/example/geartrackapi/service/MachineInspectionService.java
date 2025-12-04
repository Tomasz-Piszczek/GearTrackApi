package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.machine.dto.CreateMachineInspectionDto;
import com.example.geartrackapi.controller.machine.dto.MachineInspectionDto;
import com.example.geartrackapi.dao.model.Machine;
import com.example.geartrackapi.dao.model.MachineInspection;
import com.example.geartrackapi.dao.repository.MachineInspectionRepository;
import com.example.geartrackapi.dao.repository.MachineRepository;
import com.example.geartrackapi.mapper.MachineInspectionMapper;
import com.example.geartrackapi.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MachineInspectionService {
    
    private final MachineInspectionRepository machineInspectionRepository;
    private final MachineRepository machineRepository;
    private final MachineInspectionMapper machineInspectionMapper;
    
    @Transactional(readOnly = true)
    public Page<MachineInspectionDto> getAllInspections(Pageable pageable) {
        Page<MachineInspection> inspectionPage = machineInspectionRepository.findByOrganizationIdAndHiddenFalse(SecurityUtils.getCurrentOrganizationId(), pageable);
        List<MachineInspectionDto> inspectionDtos = inspectionPage.getContent().stream()
                .map(machineInspectionMapper::toDto)
                .toList();
        return new PageImpl<>(inspectionDtos, pageable, inspectionPage.getTotalElements());
    }
    
    @Transactional(readOnly = true)
    public Page<MachineInspectionDto> getInspectionsByMachineId(UUID machineId, Pageable pageable) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        Page<MachineInspection> inspectionPage = machineInspectionRepository.findByOrganizationIdAndMachineIdAndHiddenFalse(organizationId, machineId, pageable);
        List<MachineInspectionDto> inspectionDtos = inspectionPage.getContent().stream()
                .map(machineInspectionMapper::toDto)
                .toList();
        return new PageImpl<>(inspectionDtos, pageable, inspectionPage.getTotalElements());
    }
    
    public MachineInspectionDto createInspection(CreateMachineInspectionDto createDto) {
        Machine machine = machineRepository.findByIdAndOrganizationIdAndHiddenFalse(createDto.getMachineId(), SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new RuntimeException("Machine not found"));
        
        MachineInspection inspection = MachineInspection.builder()
                .organizationId(SecurityUtils.getCurrentOrganizationId())
                .machineId(createDto.getMachineId())
                .inspectionDate(createDto.getInspectionDate())
                .notes(createDto.getNotes())
                .status(createDto.getStatus() != null ? createDto.getStatus() : "SCHEDULED")
                .build();
        return machineInspectionMapper.toDto(machineInspectionRepository.save(inspection));
    }
    
    public MachineInspectionDto updateInspection(UUID inspectionId, CreateMachineInspectionDto updateDto) {
        MachineInspection inspection = machineInspectionRepository.findByIdAndOrganizationIdAndHiddenFalse(inspectionId, SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new RuntimeException("Inspection not found"));
        
        inspection.setMachineId(updateDto.getMachineId());
        inspection.setInspectionDate(updateDto.getInspectionDate());
        inspection.setNotes(updateDto.getNotes());
        inspection.setStatus(updateDto.getStatus() != null ? updateDto.getStatus() : inspection.getStatus());
        return machineInspectionMapper.toDto(machineInspectionRepository.save(inspection));
    }
    
    public void deleteInspection(UUID inspectionId) {
        MachineInspection inspection = machineInspectionRepository.findByIdAndOrganizationIdAndHiddenFalse(inspectionId, SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new RuntimeException("Inspection not found"));
        inspection.setHidden(true);
        machineInspectionRepository.save(inspection);
    }
    
    @Transactional(readOnly = true)
    public List<MachineInspectionDto> getInspectionHistoryByMachineId(UUID machineId) {
        return machineInspectionRepository.findByOrganizationIdAndMachineIdOrderByInspectionDateDesc(SecurityUtils.getCurrentOrganizationId(), machineId)
                .stream()
                .map(machineInspectionMapper::toDto)
                .toList();
    }
}