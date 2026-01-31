package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.badanieszkolenie.dto.BadanieSzkolenieDto;
import com.example.geartrackapi.dao.model.BadanieSzkolenie;
import com.example.geartrackapi.dao.model.BadanieSzkolenieStatus;
import com.example.geartrackapi.dao.model.Employee;
import com.example.geartrackapi.security.SecurityUtils;
import org.springframework.stereotype.Component;

@Component
public class BadanieSzkolenieMapper {

    public BadanieSzkolenieDto toDto(BadanieSzkolenie badanie) {
        return BadanieSzkolenieDto.builder()
                .id(badanie.getId())
                .employeeId(badanie.getEmployee().getId())
                .employeeFirstName(badanie.getEmployee().getFirstName())
                .employeeLastName(badanie.getEmployee().getLastName())
                .date(badanie.getDate())
                .category(badanie.getCategory())
                .status(badanie.getStatus())
                .build();
    }

    public BadanieSzkolenie toEntity(BadanieSzkolenieDto dto, Employee employee) {
        return BadanieSzkolenie.builder()
                .employee(employee)
                .date(dto.getDate())
                .category(dto.getCategory())
                .status(BadanieSzkolenieStatus.OCZEKUJACY)
                .organizationId(SecurityUtils.getCurrentOrganizationId())
                .build();
    }

    public BadanieSzkolenie updateEntity(BadanieSzkolenie existing, BadanieSzkolenieDto dto, Employee employee) {
        return BadanieSzkolenie.builder()
                .id(existing.getId())
                .employee(employee)
                .date(dto.getDate())
                .category(dto.getCategory())
                .status(dto.getStatus() != null ? dto.getStatus() : existing.getStatus())
                .organizationId(existing.getOrganizationId())
                .createdAt(existing.getCreatedAt())
                .build();
    }
}
