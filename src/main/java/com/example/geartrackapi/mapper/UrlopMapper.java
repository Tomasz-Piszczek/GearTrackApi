package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.urlop.dto.UrlopDto;
import com.example.geartrackapi.dao.model.Employee;
import com.example.geartrackapi.dao.model.Urlop;
import com.example.geartrackapi.dao.model.UrlopCategory;
import com.example.geartrackapi.dao.model.UrlopStatus;
import com.example.geartrackapi.security.SecurityUtils;
import org.springframework.stereotype.Component;

@Component
public class UrlopMapper {

    public UrlopDto toDto(Urlop urlop) {
        return UrlopDto.builder()
                .id(urlop.getId())
                .employeeId(urlop.getEmployee().getId())
                .employeeFirstName(urlop.getEmployee().getFirstName())
                .employeeLastName(urlop.getEmployee().getLastName())
                .fromDate(urlop.getFromDate())
                .toDate(urlop.getToDate())
                .note(urlop.getNote())
                .status(urlop.getStatus())
                .category(urlop.getCategory())
                .categoryRate(urlop.getCategory().getRate())
                .build();
    }

    public Urlop toEntity(UrlopDto dto, Employee employee) {
        return Urlop.builder()
                .employee(employee)
                .fromDate(dto.getFromDate())
                .toDate(dto.getToDate())
                .note(dto.getNote())
                .status(UrlopStatus.PENDING)
                .category(dto.getCategory())
                .organizationId(SecurityUtils.getCurrentOrganizationId())
                .build();
    }

    public Urlop updateEntity(Urlop existing, UrlopDto dto, Employee employee) {
        return Urlop.builder()
                .id(existing.getId())
                .employee(employee)
                .fromDate(dto.getFromDate())
                .toDate(dto.getToDate())
                .note(dto.getNote())
                .status(dto.getStatus() != null ? dto.getStatus() : existing.getStatus())
                .category(dto.getCategory() != null ? dto.getCategory() : existing.getCategory())
                .organizationId(existing.getOrganizationId())
                .createdAt(existing.getCreatedAt())
                .build();
    }
}
