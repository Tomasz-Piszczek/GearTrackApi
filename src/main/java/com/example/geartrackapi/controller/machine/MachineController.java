package com.example.geartrackapi.controller.machine;

import org.springframework.data.domain.Page;
import com.example.geartrackapi.controller.machine.dto.AssignMachineDto;
import com.example.geartrackapi.controller.machine.dto.MachineDto;
import com.example.geartrackapi.service.MachineCrudService;
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
@RequestMapping("/api/machines")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MachineController {
    
    private final MachineCrudService machineCrudService;
    
    @GetMapping
    public ResponseEntity<Page<MachineDto>> findAllMachines(Pageable pageable) {
        log.info("[findAllMachines] Getting all machines with pagination: {}", pageable);
        return ResponseEntity.ok(machineCrudService.findAllMachines(pageable));
    }

    @PostMapping
    public MachineDto createMachine(@RequestBody MachineDto machineDto) {
        log.info("[createMachine] Creating machine with name: {}", machineDto.getName());
        return machineCrudService.createMachine(machineDto);
    }
    
    @PutMapping
    public MachineDto updateMachine(@RequestBody MachineDto machineDto) {
        log.info("[updateMachine] Updating machine with UUID: {}", machineDto.getUuid());
        return machineCrudService.updateMachine(machineDto);
    }
    
    @DeleteMapping("/{id}")
    public void deleteMachine(@PathVariable UUID id) {
        log.info("[deleteMachine] Deleting machine with UUID: {}", id);
        machineCrudService.deleteMachine(id);
    }
    
    @PostMapping("/assign")
    public MachineDto assignMachineToEmployee(@RequestBody AssignMachineDto assignDto) {
        log.info("[assignMachineToEmployee] Assigning machine UUID: {} to employee UUID: {}", assignDto.getMachineId(), assignDto.getEmployeeId());
        return machineCrudService.assignMachineToEmployee(assignDto);
    }
}