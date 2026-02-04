package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.employee.dto.EmployeeUrlopDaysDto;
import com.example.geartrackapi.controller.employee.dto.VacationSummaryDto;
import com.example.geartrackapi.dao.model.Employee;
import com.example.geartrackapi.dao.model.EmployeeUrlopDays;
import com.example.geartrackapi.dao.model.Urlop;
import com.example.geartrackapi.dao.model.UrlopCategory;
import com.example.geartrackapi.dao.model.UrlopStatus;
import com.example.geartrackapi.dao.repository.EmployeeRepository;
import com.example.geartrackapi.dao.repository.EmployeeUrlopDaysRepository;
import com.example.geartrackapi.dao.repository.UrlopRepository;
import com.example.geartrackapi.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeUrlopDaysService {

    private final EmployeeUrlopDaysRepository employeeUrlopDaysRepository;
    private final EmployeeRepository employeeRepository;
    private final UrlopRepository urlopRepository;
    private final PolishWorkingDaysService polishWorkingDaysService;

    @Transactional(readOnly = true)
    public VacationSummaryDto getVacationSummary(UUID employeeId) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();

        int currentYear = Year.now().getValue();
        int previousYear = currentYear - 1;

        List<EmployeeUrlopDays> configuredDays = employeeUrlopDaysRepository
                .findByEmployeeIdAndYearInAndOrganizationIdAndHiddenFalse(
                        employeeId, List.of(currentYear, previousYear), organizationId);

        Map<Integer, Integer> daysByYear = configuredDays.stream()
                .collect(Collectors.toMap(EmployeeUrlopDays::getYear, EmployeeUrlopDays::getDays));

        List<Integer> missingYears = new ArrayList<>();
        if (!daysByYear.containsKey(previousYear)) {
            missingYears.add(previousYear);
        }
        if (!daysByYear.containsKey(currentYear)) {
            missingYears.add(currentYear);
        }

        boolean isConfigured = missingYears.isEmpty();

        if (!isConfigured) {
            return VacationSummaryDto.builder()
                    .remainingDays(null)
                    .isConfigured(false)
                    .missingYears(missingYears)
                    .build();
        }

        int currentYearAllowance = daysByYear.get(currentYear);
        int previousYearAllowance = daysByYear.get(previousYear);

        int currentYearUsed = calculateUsedVacationDays(employeeId, currentYear, organizationId);
        int previousYearUsed = calculateUsedVacationDays(employeeId, previousYear, organizationId);

        int carryOver = Math.max(0, previousYearAllowance - previousYearUsed);
        int remainingDays = currentYearAllowance + carryOver - currentYearUsed;

        return VacationSummaryDto.builder()
                .remainingDays(remainingDays)
                .isConfigured(true)
                .missingYears(missingYears)
                .build();
    }

    private int calculateUsedVacationDays(UUID employeeId, int year, UUID organizationId) {
        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEnd = LocalDate.of(year, 12, 31);

        List<Urlop> urlopy = urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId);

        return urlopy.stream()
                .filter(u -> u.getStatus() == UrlopStatus.ACCEPTED)
                .filter(u -> u.getCategory() == UrlopCategory.URLOP_WYPOCZYNKOWY)
                .mapToInt(u -> {
                    LocalDate from = u.getFromDate().isBefore(yearStart) ? yearStart : u.getFromDate();
                    LocalDate to = u.getToDate().isAfter(yearEnd) ? yearEnd : u.getToDate();

                    if (from.isAfter(to)) {
                        return 0;
                    }

                    return polishWorkingDaysService.countWorkingDays(from, to);
                })
                .sum();
    }

    @Transactional
    public EmployeeUrlopDaysDto saveUrlopDays(UUID employeeId, EmployeeUrlopDaysDto dto) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();

        Employee employee = employeeRepository.findByIdAndHiddenFalse(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        if (!employee.getOrganizationId().equals(organizationId)) {
            throw new AccessDeniedException("Employee does not belong to your organization");
        }

        EmployeeUrlopDays entity = employeeUrlopDaysRepository
                .findByEmployeeIdAndYearAndOrganizationIdAndHiddenFalse(employeeId, dto.getYear(), organizationId)
                .map(existing -> {
                    existing.setDays(dto.getDays());
                    return existing;
                })
                .orElseGet(() -> EmployeeUrlopDays.builder()
                        .employee(employee)
                        .year(dto.getYear())
                        .days(dto.getDays())
                        .organizationId(organizationId)
                        .build());

        EmployeeUrlopDays saved = employeeUrlopDaysRepository.save(entity);
        return toDto(saved);
    }

    @Transactional
    public List<EmployeeUrlopDaysDto> saveMultipleUrlopDays(UUID employeeId, List<EmployeeUrlopDaysDto> dtos) {
        return dtos.stream()
                .map(dto -> saveUrlopDays(employeeId, dto))
                .collect(Collectors.toList());
    }

    private EmployeeUrlopDaysDto toDto(EmployeeUrlopDays entity) {
        return EmployeeUrlopDaysDto.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployee().getId())
                .year(entity.getYear())
                .days(entity.getDays())
                .build();
    }
}
