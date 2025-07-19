package com.example.geartrackapi.controller.machine;

import com.example.geartrackapi.controller.common.dto.PagedResponse;
import com.example.geartrackapi.controller.machine.dto.AssignMachineDto;
import com.example.geartrackapi.controller.machine.dto.MachineDto;
import com.example.geartrackapi.service.MachineCrudService;
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
@RequestMapping("/api/machines")
@RequiredArgsConstructor
public class MachineController {
    
    private final MachineCrudService machineCrudService;
    
    @GetMapping
    public ResponseEntity<PagedResponse<MachineDto>> findAllMachines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        log.info("[findAllMachines] Getting all machines - page: {}, size: {}, sortBy: {}, direction: {}", 
                page, size, sortBy, sortDirection);
        
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<MachineDto> machines = machineCrudService.findAllMachines(pageable);
        
        return ResponseEntity.ok(machines);
    }
    
    @GetMapping("/all")
    public List<MachineDto> findAllMachinesNonPaginated() {
        log.info("[findAllMachinesNonPaginated] Getting all machines without pagination");
        return machineCrudService.findAllMachinesNonPaginated();
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