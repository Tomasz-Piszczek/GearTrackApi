package com.example.geartrackapi.controller.machine;

import com.example.geartrackapi.controller.common.dto.PagedResponse;
import com.example.geartrackapi.controller.machine.dto.CreateMachineInspectionDto;
import com.example.geartrackapi.controller.machine.dto.MachineInspectionDto;
import com.example.geartrackapi.service.MachineInspectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/machine-inspections")
@RequiredArgsConstructor
public class MachineInspectionController {
    
    private final MachineInspectionService machineInspectionService;
    
    @GetMapping
    public ResponseEntity<PagedResponse<MachineInspectionDto>> getAllInspections(Pageable pageable) {
        log.info("Getting all machine inspections with pagination: {}", pageable);
        PagedResponse<MachineInspectionDto> inspections = machineInspectionService.getAllInspections(pageable);
        return ResponseEntity.ok(inspections);
    }
    
    @GetMapping("/machine/{machineId}")
    public ResponseEntity<PagedResponse<MachineInspectionDto>> getInspectionsByMachineId(
            @PathVariable UUID machineId, Pageable pageable) {
        log.info("Getting inspections for machine {} with pagination: {}", machineId, pageable);
        PagedResponse<MachineInspectionDto> inspections = machineInspectionService.getInspectionsByMachineId(machineId, pageable);
        return ResponseEntity.ok(inspections);
    }
    
    @GetMapping("/machine/{machineId}/history")
    public ResponseEntity<List<MachineInspectionDto>> getInspectionHistory(@PathVariable UUID machineId) {
        log.info("Getting inspection history for machine {}", machineId);
        return ResponseEntity.ok(machineInspectionService.getInspectionHistoryByMachineId(machineId));
    }
    
    @PostMapping
    public ResponseEntity<MachineInspectionDto> createInspection(@RequestBody CreateMachineInspectionDto createDto) {
        log.info("Creating new machine inspection for machine {}", createDto.getMachineId());
        return ResponseEntity.ok(machineInspectionService.createInspection(createDto));
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