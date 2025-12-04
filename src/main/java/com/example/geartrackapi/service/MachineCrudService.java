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
        Page<Machine> machinePage = machineRepository.findByOrganizationIdAndHiddenFalse(SecurityUtils.getCurrentOrganizationId(), pageable);
        List<MachineDto> machineDtos = machinePage.getContent()
                .stream()
                .map(machineMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(machineDtos, pageable, machinePage.getTotalElements());
    }
    
    public MachineDto createMachine(MachineDto machineDto) {
        Machine machine = machineMapper.toEntity(machineDto);
        machine.setOrganizationId(SecurityUtils.getCurrentOrganizationId());
        return machineMapper.toDto(machineRepository.save(machine));
    }
    
    public MachineDto updateMachine(MachineDto machineDto) {
        Machine machine = machineRepository.findByIdAndOrganizationIdAndHiddenFalse(machineDto.getUuid(), SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new EntityNotFoundException("Machine not found with UUID: " + machineDto.getUuid()));
        machineMapper.updateEntity(machine, machineDto);
        return machineMapper.toDto(machineRepository.save(machine));
    }
    
    public void deleteMachine(UUID id) {
        Machine machine = machineRepository.findByIdAndOrganizationIdAndHiddenFalse(id, SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new EntityNotFoundException("Machine not found with UUID: " + id));
        machine.setHidden(true);
        machineRepository.save(machine);
    }
    
    public MachineDto assignMachineToEmployee(AssignMachineDto assignDto) {
        Machine machine = machineRepository.findByIdAndOrganizationIdAndHiddenFalse(assignDto.getMachineId(), SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new EntityNotFoundException("Machine not found with UUID: " + assignDto.getMachineId()));
        machine.setEmployeeId(assignDto.getEmployeeId());
        return machineMapper.toDto(machineRepository.save(machine));
    }
}