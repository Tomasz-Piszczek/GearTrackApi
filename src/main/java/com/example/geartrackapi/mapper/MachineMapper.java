package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.machine.dto.MachineDto;
import com.example.geartrackapi.dao.model.Machine;
import org.springframework.stereotype.Component;

@Component
public class MachineMapper {
    
    public MachineDto toDto(Machine machine) {
        String employeeName = null;
        if (machine.getEmployee() != null) {
            employeeName = machine.getEmployee().getFirstName() + " " + machine.getEmployee().getLastName();
        }
        
        return MachineDto.builder()
                .uuid(machine.getUuid())
                .name(machine.getName())
                .factoryNumber(machine.getFactoryNumber())
                .employeeId(machine.getEmployeeId())
                .employeeName(employeeName)
                .build();
    }
    
    public Machine toEntity(MachineDto dto) {
        Machine machine = new Machine();
        machine.setName(dto.getName());
        machine.setFactoryNumber(dto.getFactoryNumber());
        machine.setEmployeeId(dto.getEmployeeId());
        return machine;
    }
    
    public void updateEntity(Machine machine, MachineDto dto) {
        machine.setName(dto.getName());
        machine.setFactoryNumber(dto.getFactoryNumber());
        machine.setEmployeeId(dto.getEmployeeId());
    }
}