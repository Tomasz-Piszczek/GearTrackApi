package com.example.geartrackapi.controller.machine;

import com.example.geartrackapi.controller.common.dto.PagedResponse;
import com.example.geartrackapi.controller.machine.dto.CreateMachineInspectionDto;
import com.example.geartrackapi.controller.machine.dto.MachineInspectionDto;
import com.example.geartrackapi.service.MachineInspectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<PagedResponse<MachineInspectionDto>> getAllInspections(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "inspectionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        log.info("Getting all machine inspections - page: {}, size: {}, sortBy: {}, direction: {}", 
                page, size, sortBy, sortDirection);
        
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<MachineInspectionDto> inspections = machineInspectionService.getAllInspections(pageable);
        
        return ResponseEntity.ok(inspections);
    }
    
    @GetMapping("/machine/{machineId}")
    public ResponseEntity<PagedResponse<MachineInspectionDto>> getInspectionsByMachineId(
            @PathVariable UUID machineId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "inspectionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        log.info("Getting inspections for machine {} - page: {}, size: {}", machineId, page, size);
        
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<MachineInspectionDto> inspections = machineInspectionService.getInspectionsByMachineId(machineId, pageable);
        
        return ResponseEntity.ok(inspections);
    }
    
    @GetMapping("/machine/{machineId}/history")
    public ResponseEntity<List<MachineInspectionDto>> getInspectionHistory(@PathVariable UUID machineId) {
        log.info("Getting inspection history for machine {}", machineId);
        
        List<MachineInspectionDto> inspections = machineInspectionService.getInspectionHistoryByMachineId(machineId);
        return ResponseEntity.ok(inspections);
    }
    
    @PostMapping
    public ResponseEntity<MachineInspectionDto> createInspection(@RequestBody CreateMachineInspectionDto createDto) {
        log.info("Creating new machine inspection for machine {}", createDto.getMachineId());
        
        MachineInspectionDto createdInspection = machineInspectionService.createInspection(createDto);
        return ResponseEntity.ok(createdInspection);
    }
    
    @PutMapping("/{inspectionId}")
    public ResponseEntity<MachineInspectionDto> updateInspection(
            @PathVariable UUID inspectionId,
            @RequestBody CreateMachineInspectionDto updateDto
    ) {
        log.info("Updating machine inspection {}", inspectionId);
        
        MachineInspectionDto updatedInspection = machineInspectionService.updateInspection(inspectionId, updateDto);
        return ResponseEntity.ok(updatedInspection);
    }
    
    @DeleteMapping("/{inspectionId}")
    public ResponseEntity<Void> deleteInspection(@PathVariable UUID inspectionId) {
        log.info("Deleting machine inspection {}", inspectionId);
        
        machineInspectionService.deleteInspection(inspectionId);
        return ResponseEntity.noContent().build();
    }
}