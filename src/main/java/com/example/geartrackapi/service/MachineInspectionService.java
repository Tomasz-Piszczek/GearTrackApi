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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MachineInspectionService {

    private final MachineInspectionRepository machineInspectionRepository;
    private final MachineRepository machineRepository;
    private final MachineInspectionMapper machineInspectionMapper;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    
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
    
    public MachineInspectionDto createInspection(UUID machineId, CreateMachineInspectionDto createDto) {
        Machine machine = machineRepository.findByIdAndOrganizationIdAndHiddenFalse(machineId, SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new RuntimeException("Machine not found"));

        MachineInspection inspection = MachineInspection.builder()
                .organizationId(SecurityUtils.getCurrentOrganizationId())
                .machineId(machineId)
                .inspectionDate(createDto.getInspectionDate())
                .notes(createDto.getNotes())
                .status(createDto.getStatus() != null ? createDto.getStatus() : "SCHEDULED")
                .performedBy(createDto.getPerformedBy())
                .build();
        MachineInspectionDto savedDto = machineInspectionMapper.toDto(machineInspectionRepository.saveAndFlush(inspection));
        emitEvent("CREATE", savedDto);
        return savedDto;
    }

    public MachineInspectionDto updateInspection(UUID inspectionId, CreateMachineInspectionDto updateDto) {
        MachineInspection existing = machineInspectionRepository.findByIdAndOrganizationIdAndHiddenFalse(inspectionId, SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new RuntimeException("Inspection not found"));

        MachineInspection updated = machineInspectionMapper.updateEntity(existing, updateDto);
        MachineInspectionDto savedDto = machineInspectionMapper.toDto(machineInspectionRepository.saveAndFlush(updated));
        emitEvent("UPDATE", savedDto);
        return savedDto;
    }

    public void deleteInspection(UUID inspectionId) {
        MachineInspection inspection = machineInspectionRepository.findByIdAndOrganizationIdAndHiddenFalse(inspectionId, SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new RuntimeException("Inspection not found"));
        inspection.setHidden(true);
        machineInspectionRepository.save(inspection);
        MachineInspectionDto deletedDto = machineInspectionMapper.toDto(inspection);
        emitEvent("DELETE", deletedDto);
    }

    @Transactional(readOnly = true)
    public List<MachineInspectionDto> getInspectionHistoryByMachineId(UUID machineId) {
        return machineInspectionRepository.findByOrganizationIdAndMachineIdOrderByInspectionDateDesc(SecurityUtils.getCurrentOrganizationId(), machineId)
                .stream()
                .map(machineInspectionMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MachineInspectionDto> getAllScheduledInspections() {
        return machineInspectionRepository.findAllScheduledByOrganizationId(SecurityUtils.getCurrentOrganizationId())
                .stream()
                .map(machineInspectionMapper::toDto)
                .toList();
    }

    public SseEmitter subscribe() {
        log.info("[subscribe] Creating new SSE emitter for machine inspection updates");

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        emitter.onCompletion(() -> {
            log.info("[subscribe] SSE emitter completed");
            emitters.remove(emitter);
        });
        emitter.onTimeout(() -> {
            log.info("[subscribe] SSE emitter timed out");
            emitters.remove(emitter);
        });
        emitter.onError((e) -> {
            log.error("[subscribe] SSE emitter error", e);
            emitters.remove(emitter);
        });

        return emitter;
    }

    private void emitEvent(String eventType, MachineInspectionDto dto) {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventType)
                        .data(dto));
            } catch (IOException e) {
                log.error("Error sending SSE event", e);
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }
}