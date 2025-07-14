package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.machine.dto.AssignMachineDto;
import com.example.geartrackapi.controller.machine.dto.MachineDto;
import com.example.geartrackapi.dao.model.Machine;
import com.example.geartrackapi.dao.repository.MachineRepository;
import com.example.geartrackapi.mapper.MachineMapper;
import com.example.geartrackapi.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MachineCrudService {
    
    private final MachineRepository machineRepository;
    private final MachineMapper machineMapper;
    
    public List<MachineDto> findAllMachines() {
        log.debug("[findAllMachines] Getting all machines for authenticated user");
        UUID userId = SecurityUtils.authenticatedUserId();
        return machineRepository.findByUserId(userId)
                .stream()
                .map(machineMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public MachineDto createMachine(MachineDto machineDto) {
        log.debug("[createMachine] Creating machine with name: {}", machineDto.getName());
        UUID userId = SecurityUtils.authenticatedUserId();
        Machine machine = machineMapper.toEntity(machineDto);
        machine.setUserId(userId);
        Machine savedMachine = machineRepository.save(machine);
        return machineMapper.toDto(savedMachine);
    }
    
    public MachineDto updateMachine(MachineDto machineDto) {
        log.debug("[updateMachine] Updating machine with UUID: {}", machineDto.getUuid());
        UUID userId = SecurityUtils.authenticatedUserId();
        Machine machine = machineRepository.findById(machineDto.getUuid())
                .orElseThrow(() -> new EntityNotFoundException("Machine not found with UUID: " + machineDto.getUuid()));
        
        if (!machine.getUserId().equals(userId)) {
            throw new EntityNotFoundException("Machine not found with UUID: " + machineDto.getUuid());
        }
        
        machineMapper.updateEntity(machine, machineDto);
        Machine savedMachine = machineRepository.save(machine);
        return machineMapper.toDto(savedMachine);
    }
    
    public void deleteMachine(UUID id) {
        log.debug("[deleteMachine] Deleting machine with UUID: {}", id);
        UUID userId = SecurityUtils.authenticatedUserId();
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Machine not found with UUID: " + id));
        
        if (!machine.getUserId().equals(userId)) {
            throw new EntityNotFoundException("Machine not found with UUID: " + id);
        }
        
        machineRepository.delete(machine);
    }
    
    public MachineDto assignMachineToEmployee(AssignMachineDto assignDto) {
        log.debug("[assignMachineToEmployee] Assigning machine UUID: {} to employee UUID: {}", assignDto.getMachineId(), assignDto.getEmployeeId());
        UUID userId = SecurityUtils.authenticatedUserId();
        Machine machine = machineRepository.findById(assignDto.getMachineId())
                .orElseThrow(() -> new EntityNotFoundException("Machine not found with UUID: " + assignDto.getMachineId()));
        
        if (!machine.getUserId().equals(userId)) {
            throw new EntityNotFoundException("Machine not found with UUID: " + assignDto.getMachineId());
        }
        
        machine.setEmployeeId(assignDto.getEmployeeId());
        Machine savedMachine = machineRepository.save(machine);
        return machineMapper.toDto(savedMachine);
    }
}