package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.common.dto.PagedResponse;
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
    public PagedResponse<MachineInspectionDto> getAllInspections(Pageable pageable) {
        UUID userId = SecurityUtils.authenticatedUserId();
        Page<MachineInspection> inspectionPage = machineInspectionRepository.findByUserId(userId, pageable);
        
        List<MachineInspectionDto> inspectionDtos = inspectionPage.getContent().stream()
                .map(machineInspectionMapper::toDto)
                .toList();
        
        return PagedResponse.of(
                inspectionDtos,
                inspectionPage.getNumber(),
                inspectionPage.getSize(),
                inspectionPage.getTotalElements(),
                inspectionPage.getTotalPages(),
                inspectionPage.isFirst(),
                inspectionPage.isLast(),
                inspectionPage.isEmpty()
        );
    }
    
    @Transactional(readOnly = true)
    public PagedResponse<MachineInspectionDto> getInspectionsByMachineId(UUID machineId, Pageable pageable) {
        UUID userId = SecurityUtils.authenticatedUserId();
        
        // Verify machine belongs to user
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new RuntimeException("Machine not found"));
        
        if (!machine.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        Page<MachineInspection> inspectionPage = machineInspectionRepository.findByUserIdAndMachineId(userId, machineId, pageable);
        
        List<MachineInspectionDto> inspectionDtos = inspectionPage.getContent().stream()
                .map(machineInspectionMapper::toDto)
                .toList();
        
        return PagedResponse.of(
                inspectionDtos,
                inspectionPage.getNumber(),
                inspectionPage.getSize(),
                inspectionPage.getTotalElements(),
                inspectionPage.getTotalPages(),
                inspectionPage.isFirst(),
                inspectionPage.isLast(),
                inspectionPage.isEmpty()
        );
    }
    
    public MachineInspectionDto createInspection(CreateMachineInspectionDto createDto) {
        UUID userId = SecurityUtils.authenticatedUserId();
        
        // Verify machine belongs to user
        Machine machine = machineRepository.findById(createDto.getMachineId())
                .orElseThrow(() -> new RuntimeException("Machine not found"));
        
        if (!machine.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        MachineInspection inspection = MachineInspection.builder()
                .machineId(createDto.getMachineId())
                .inspectionDate(createDto.getInspectionDate())
                .performedBy(createDto.getPerformedBy())
                .notes(createDto.getNotes())
                .status(createDto.getStatus() != null ? createDto.getStatus() : "COMPLETED")
                .build();
        
        MachineInspection savedInspection = machineInspectionRepository.save(inspection);
        log.info("Created machine inspection for machine {} by user {}", createDto.getMachineId(), userId);
        
        return machineInspectionMapper.toDto(savedInspection);
    }
    
    public MachineInspectionDto updateInspection(UUID inspectionId, CreateMachineInspectionDto updateDto) {
        UUID userId = SecurityUtils.authenticatedUserId();
        
        MachineInspection inspection = machineInspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new RuntimeException("Inspection not found"));
        
        if (!inspection.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        // Verify machine belongs to user
        Machine machine = machineRepository.findById(updateDto.getMachineId())
                .orElseThrow(() -> new RuntimeException("Machine not found"));
        
        if (!machine.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        inspection.setMachineId(updateDto.getMachineId());
        inspection.setInspectionDate(updateDto.getInspectionDate());
        inspection.setPerformedBy(updateDto.getPerformedBy());
        inspection.setNotes(updateDto.getNotes());
        inspection.setStatus(updateDto.getStatus() != null ? updateDto.getStatus() : inspection.getStatus());
        
        MachineInspection updatedInspection = machineInspectionRepository.save(inspection);
        log.info("Updated machine inspection {} by user {}", inspectionId, userId);
        
        return machineInspectionMapper.toDto(updatedInspection);
    }
    
    public void deleteInspection(UUID inspectionId) {
        UUID userId = SecurityUtils.authenticatedUserId();
        
        MachineInspection inspection = machineInspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new RuntimeException("Inspection not found"));
        
        if (!inspection.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        machineInspectionRepository.delete(inspection);
        log.info("Deleted machine inspection {} by user {}", inspectionId, userId);
    }
    
    @Transactional(readOnly = true)
    public List<MachineInspectionDto> getInspectionHistoryByMachineId(UUID machineId) {
        UUID userId = SecurityUtils.authenticatedUserId();
        
        // Verify machine belongs to user
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new RuntimeException("Machine not found"));
        
        if (!machine.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        List<MachineInspection> inspections = machineInspectionRepository.findByUserIdAndMachineIdOrderByInspectionDateDesc(userId, machineId);
        
        return inspections.stream()
                .map(machineInspectionMapper::toDto)
                .toList();
    }
}