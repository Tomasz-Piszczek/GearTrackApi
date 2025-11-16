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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    
    public Page<MachineDto> findAllMachines(Pageable pageable) {
        log.debug("[findAllMachines] Getting paginated machines for authenticated user");
        Page<Machine> machinePage = machineRepository.findByUserId(SecurityUtils.authenticatedUserId(), pageable);
        List<MachineDto> machineDtos = machinePage.getContent()
                .stream()
                .map(machineMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(machineDtos, pageable, machinePage.getTotalElements());
    }
    
    public MachineDto createMachine(MachineDto machineDto) {
        log.debug("[createMachine] Creating machine with name: {}", machineDto.getName());
        Machine machine = machineMapper.toEntity(machineDto);
        machine.setUserId(SecurityUtils.authenticatedUserId());
        return machineMapper.toDto(machineRepository.save(machine));
    }
    
    public MachineDto updateMachine(MachineDto machineDto) {
        log.debug("[updateMachine] Updating machine with UUID: {}", machineDto.getUuid());
        Machine machine = machineRepository.findById(machineDto.getUuid())
                .orElseThrow(() -> new EntityNotFoundException("Machine not found with UUID: " + machineDto.getUuid()));
        machineMapper.updateEntity(machine, machineDto);
        return machineMapper.toDto(machineRepository.save(machine));
    }
    
    public void deleteMachine(UUID id) {
        log.debug("[deleteMachine] Deleting machine with UUID: {}", id);
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Machine not found with UUID: " + id));
        machineRepository.delete(machine);
    }
    
    public MachineDto assignMachineToEmployee(AssignMachineDto assignDto) {
        log.debug("[assignMachineToEmployee] Assigning machine UUID: {} to employee UUID: {}", assignDto.getMachineId(), assignDto.getEmployeeId());
        Machine machine = machineRepository.findById(assignDto.getMachineId())
                .orElseThrow(() -> new EntityNotFoundException("Machine not found with UUID: " + assignDto.getMachineId()));
        machine.setEmployeeId(assignDto.getEmployeeId());
        return machineMapper.toDto(machineRepository.save(machine));
    }
}