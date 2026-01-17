package com.example.geartrackapi.controller.machine;

import org.springframework.data.domain.Page;
import com.example.geartrackapi.controller.machine.dto.CreateMachineInspectionDto;
import com.example.geartrackapi.controller.machine.dto.MachineInspectionDto;
import com.example.geartrackapi.service.MachineInspectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/machine-inspections")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MachineInspectionController {
    
    private final MachineInspectionService machineInspectionService;
    
    @GetMapping
    public ResponseEntity<Page<MachineInspectionDto>> getAllInspections(Pageable pageable) {
        log.info("Getting all machine inspections with pagination: {}", pageable);
        return ResponseEntity.ok(machineInspectionService.getAllInspections(pageable));
    }
    
    @GetMapping("/machine/{machineId}")
    public ResponseEntity<Page<MachineInspectionDto>> getInspectionsByMachineId(
            @PathVariable UUID machineId, Pageable pageable) {
        log.info("Getting inspections for machine {} with pagination: {}", machineId, pageable);
        return ResponseEntity.ok(machineInspectionService.getInspectionsByMachineId(machineId, pageable));
    }
    
    @GetMapping("/machine/{machineId}/history")
    public ResponseEntity<List<MachineInspectionDto>> getInspectionHistory(@PathVariable UUID machineId) {
        log.info("Getting inspection history for machine {}", machineId);
        return ResponseEntity.ok(machineInspectionService.getInspectionHistoryByMachineId(machineId));
    }
    
    @PostMapping("/{machineId}")
    public ResponseEntity<MachineInspectionDto> createInspection(@PathVariable UUID machineId, @RequestBody CreateMachineInspectionDto createDto) {
        log.info("Creating new machine inspection for machine {}", machineId);
        return ResponseEntity.ok(machineInspectionService.createInspection(machineId, createDto));
    }
    
    @PutMapping("/{inspectionId}")
    public ResponseEntity<MachineInspectionDto> updateInspection(
            @PathVariable UUID inspectionId, @RequestBody CreateMachineInspectionDto updateDto) {
        log.info("Updating machine inspection {}", inspectionId);
        return ResponseEntity.ok(machineInspectionService.updateInspection(inspectionId, updateDto));
    }
    
    @DeleteMapping("/{inspectionId}")
    public ResponseEntity<Void> deleteInspection(@PathVariable UUID inspectionId) {
        log.info("Deleting machine inspection {}", inspectionId);
        machineInspectionService.deleteInspection(inspectionId);
        return ResponseEntity.noContent().build();
    }
}